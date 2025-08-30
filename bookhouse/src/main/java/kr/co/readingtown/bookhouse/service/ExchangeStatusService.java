package kr.co.readingtown.bookhouse.service;

import kr.co.readingtown.bookhouse.domain.Bookhouse;
import kr.co.readingtown.bookhouse.domain.ExchangeStatus;
import kr.co.readingtown.bookhouse.domain.enums.IsExchanged;
import kr.co.readingtown.bookhouse.domain.enums.RequestStatus;
import kr.co.readingtown.bookhouse.dto.request.ExchangeRequestDto;
import kr.co.readingtown.bookhouse.dto.response.AcceptExchangeResponseDto;
import kr.co.readingtown.bookhouse.dto.response.ExchangeResponseDto;
import kr.co.readingtown.bookhouse.dto.response.ExchangedBookResponse;
import kr.co.readingtown.bookhouse.dto.response.ExchangedDetail;
import kr.co.readingtown.bookhouse.exception.BookhouseException;
import kr.co.readingtown.bookhouse.repository.BookhouseRepository;
import kr.co.readingtown.bookhouse.repository.ExchangeStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeStatusService {

    private final BookhouseRepository bookhouseRepository;
    private final ExchangeStatusRepository exchangeStatusRepository;

    public ExchangedBookResponse getBookIdByChatroomId(Long chatroomId, Long myId) {

        List<ExchangeStatus> exchangeStatusList = exchangeStatusRepository.findByChatroomId(chatroomId);

        ExchangedDetail myBook = null;
        ExchangedDetail partnerBook = null;

        for (ExchangeStatus status : exchangeStatusList) {
            Bookhouse bookhouse = bookhouseRepository.findById(status.getBookhouseId())
                    .orElseThrow(BookhouseException.BookhouseNotFound::new);

            if (bookhouse.getMemberId().equals(myId)) {
                myBook = new ExchangedDetail(
                        status.getExchangeStatusId(),
                        bookhouse.getBookhouseId(),
                        bookhouse.getBookId(),
                        bookhouse.getIsExchanged().toString()
                );
            } else {
                partnerBook = new ExchangedDetail(
                        status.getExchangeStatusId(),
                        bookhouse.getBookhouseId(),
                        bookhouse.getBookId(),
                        bookhouse.getIsExchanged().toString()
                );
            }
        }

        return new ExchangedBookResponse(myBook, partnerBook);
    }

    // 교환 요청 생성
    @Transactional
    public ExchangeResponseDto createExchangeStatus(Long memberId, ExchangeRequestDto exchangeRequestDto) {

        boolean exists = exchangeStatusRepository.existsByChatroomIdAndBookhouseId(exchangeRequestDto.chatroomId(), exchangeRequestDto.bookhouseId());
        if (exists) { throw new BookhouseException.DuplicateExchangeRequest();}

        // 이미 bookhouse의 isExchanged 칸이 null 이 아니거나, PENDING이 아닐 때 교환 생성 불가(예약 or 교환중)
        Bookhouse bookhouse = bookhouseRepository.findById(exchangeRequestDto.bookhouseId())
                .orElseThrow(BookhouseException.BookhouseNotFound::new);

        if (bookhouse.getIsExchanged() == null || bookhouse.getIsExchanged() != IsExchanged.PENDING) {
            throw new BookhouseException.InvalidExchangeStatusForRequest();
        }

        ExchangeStatus newExchangeStatus = ExchangeStatus.builder()
                .chatroomId(exchangeRequestDto.chatroomId())
                .bookhouseId(exchangeRequestDto.bookhouseId())
                .requestStatus(RequestStatus.REQUEST)
                .build();

        Long exchangeStatusId = exchangeStatusRepository.save(newExchangeStatus).getExchangeStatusId();

        return new ExchangeResponseDto(exchangeStatusId, newExchangeStatus.getRequestStatus());
    }

    // 교환 요청 수락
    @Transactional
    public AcceptExchangeResponseDto acceptExchangeStatus(Long memberId, Long exchangeStatusId) {

        ExchangeStatus exchangeStatus = exchangeStatusRepository.findByIdForUpdate(exchangeStatusId)
                .orElseThrow(BookhouseException.ExchangeStatusNotFound::new);

        Bookhouse bookhouse = bookhouseRepository.findById(exchangeStatus.getBookhouseId())
                .orElseThrow(BookhouseException.BookhouseNotFound::new);

        // memberId 가 해당 bookhouse의 소유자인지 검증
        if (!bookhouse.getMemberId().equals(memberId)) {
            throw new BookhouseException.ForbiddenAcceptRequest();
        }

        // 1) 채팅방의 모든 교환행을 잠금
        List<ExchangeStatus> allInRoom = exchangeStatusRepository.findAllByChatroomIdForUpdate(exchangeStatus.getChatroomId());


        // 2) Request 상태에서만 교환 요청 수락 가능
        if (exchangeStatus.getRequestStatus() != RequestStatus.REQUEST) {
            throw new BookhouseException.InvalidExchangeStatusForTransition();
        }

        // 3) 수락으로 전환
        exchangeStatus.updateRequestStatus(RequestStatus.ACCEPTED);

        // 4) 같은 트랜잭션/잠금 하에서 메모리 상으로 ACCEPTED 개수 계산
        long acceptedCount = allInRoom.stream()
                .map(ExchangeStatus::getRequestStatus)
                .filter(RequestStatus.ACCEPTED::equals)
                .count();

        boolean reserved = false;

        if (acceptedCount >= 2) {
            if (acceptedCount != 2) {
                throw new BookhouseException.DomainInvariantBroken();
            }
            // 5) 두 책 예약(RESERVED) 전환 (Bookhouse도 잠금)
            List<Long> bookhouseIds = allInRoom.stream()
                    .filter(es -> es.getRequestStatus() == RequestStatus.ACCEPTED)
                    .map(ExchangeStatus::getBookhouseId)
                    .toList();

            List<Bookhouse> books = bookhouseRepository.findAllByIdForUpdate(bookhouseIds);
            for (Bookhouse b : books) {
                //이미 예약된 책에 대해 교환 수락을 시도하는 경우, 다시 REQUEST 상태로 되돌려놓음
                if (b.getIsExchanged() == IsExchanged.RESERVED){
                    exchangeStatus.updateRequestStatus(RequestStatus.REQUEST);
                    throw new BookhouseException.BookAlreadyReserved();
                }
                b.updateIsExchanged(IsExchanged.RESERVED);
                b.updateChatroomId(exchangeStatus.getChatroomId());
            }
            reserved = true;
        }

        return new AcceptExchangeResponseDto(exchangeStatus.getRequestStatus(), reserved);
    }

    // 교환 요청 거절
    @Transactional
    public RequestStatus rejectExchangeStatus(Long memberId, Long exchangeRequestId) {

        ExchangeStatus exchangeStatus = exchangeStatusRepository.findByIdForUpdate(exchangeRequestId)
                .orElseThrow(BookhouseException.ExchangeStatusNotFound::new);

        Bookhouse bookhouse = bookhouseRepository.findById(exchangeStatus.getBookhouseId())
                .orElseThrow(BookhouseException.BookhouseNotFound::new);

        // memberId 가 해당 bookhouse의 소유자인지 검증
        if (!bookhouse.getMemberId().equals(memberId)) {
            throw new BookhouseException.ForbiddenAcceptRequest();
        }

        if (exchangeStatus.getRequestStatus() != RequestStatus.REQUEST) {
            throw new BookhouseException.InvalidExchangeTransitionReject();
        }

        exchangeStatus.updateRequestStatus(RequestStatus.REJECTED);
        return exchangeStatus.getRequestStatus();
    }

    // 교환 요청 취소
    @Transactional
    public void cancelExchangeStatus(Long memberId, Long exchangeRequestId) {
        ExchangeStatus exchangeStatus = exchangeStatusRepository.findByIdForUpdate(exchangeRequestId)
                .orElseThrow(BookhouseException.ExchangeStatusNotFound::new);

        Bookhouse bookhouse = bookhouseRepository.findById(exchangeStatus.getBookhouseId())
                .orElseThrow(BookhouseException.BookhouseNotFound::new);

        // 요청 대상 책의 소유자는 취소할 수 없음 (요청자만 취소 가능)
        if (bookhouse.getMemberId().equals(memberId)) {
            throw new BookhouseException.ForbiddenCancelByBookOwner();
        }

        if (exchangeStatus.getRequestStatus() != RequestStatus.REQUEST) {
            throw new BookhouseException.InvalidExchangeTransitionCancel();
        }

        exchangeStatusRepository.delete(exchangeStatus);
    }

    // 교환 시작 - RESERVED -> EXCHANGED
    @Transactional
    public void completeExchange(Long chatroomId) {
        List<ExchangeStatus> exchangeStatusList = exchangeStatusRepository.findAllByChatroomIdForUpdate(chatroomId);


        if (exchangeStatusList.size() != 2) {
            throw new BookhouseException.InvalidExchangeStatusCount();
        }

        // 두 개의 Bookhouse를 모두 RESERVED -> EXCHANGED로 변경
        List<Long> bookhouseIds = exchangeStatusList.stream().map(ExchangeStatus::getBookhouseId).toList();
        List<Bookhouse> books = bookhouseRepository.findAllByIdForUpdate(bookhouseIds);

        for (Bookhouse b : books) {
            if (b.getIsExchanged() != IsExchanged.RESERVED) {
                throw new BookhouseException.InvalidExchangeStatusForComplete();
            }
        }
        books.forEach(b -> b.updateIsExchanged(IsExchanged.EXCHANGED));
    }

    // 교환 반납 - EXCHANGED -> PENDING
    @Transactional
    public void returnExchange(Long chatroomId) {
        List<ExchangeStatus> exchangeStatusList =
                exchangeStatusRepository.findAllByChatroomIdForUpdate(chatroomId);

        if (exchangeStatusList.size() != 2) {
            throw new BookhouseException.InvalidExchangeStatusCount();
        }

        List<Long> ids = exchangeStatusList.stream().map(ExchangeStatus::getBookhouseId).toList();
        List<Bookhouse> books = bookhouseRepository.findAllByIdForUpdate(ids);

        for (Bookhouse b : books) {
            if (b.getIsExchanged() != IsExchanged.EXCHANGED) {
                throw new BookhouseException.InvalidExchangeStatusForReturn();
            }
        }
        books.forEach(b -> {
            b.updateIsExchanged(IsExchanged.PENDING);
            b.updateChatroomId(null);
        });
        
        // TODO ExchangeStatus 레코드 삭제 필요할지
    }
}
