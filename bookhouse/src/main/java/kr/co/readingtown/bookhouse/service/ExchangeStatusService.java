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

        boolean exists = exchangeStatusRepository.existsByChatroomIdAndBookhouseId(
                exchangeRequestDto.chatroomId(),
                exchangeRequestDto.bookhouseId()
        );

        if (exists) {
            throw new BookhouseException.DuplicateExchangeRequest();
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

        ExchangeStatus exchangeStatus = exchangeStatusRepository.findById(exchangeStatusId)
                .orElseThrow(BookhouseException.ExchangeStatusNotFound::new);

        Bookhouse bookhouse = bookhouseRepository.findById(exchangeStatus.getBookhouseId())
                .orElseThrow(BookhouseException.BookhouseNotFound::new);

        // memberId 가 해당 bookhouse의 소유자인지 검증
        if (!bookhouse.getMemberId().equals(memberId)) {
            throw new BookhouseException.ForbiddenAcceptRequest();
        }

        // Request 상태에서만 교환 요청 수락 가능
        if (exchangeStatus.getRequestStatus() != RequestStatus.REQUEST) {
            throw new BookhouseException.InvalidExchangeStatusForTransition();
        }

        exchangeStatus.updateRequestStatus(RequestStatus.ACCEPTED);

        // 같은 채팅방 내 ACCEPTED 개수 확인하여 예약 중으로 변경
        List<ExchangeStatus> acceptedInRoom = exchangeStatusRepository.findAllAcceptedByChatRoomId(
                exchangeStatus.getChatroomId(),
                RequestStatus.ACCEPTED
        );

        if (acceptedInRoom.size() >= 2) {
            if (acceptedInRoom.size() != 2) {
                throw new BookhouseException.DomainInvariantBroken();
            }

            // 두 건의 bookhouse에 예약(RESERVED) 상태, 채팅룸id 저장
            List<Long> bookhouseIds = acceptedInRoom.stream()
                    .map(ExchangeStatus::getBookhouseId)
                    .toList();

            List<Bookhouse> bookhouses = bookhouseRepository.findAllById(bookhouseIds);

            // 해당 채팅방으로 예약 상태 설정
            for (Bookhouse reservedBookhouse : bookhouses) {
                reservedBookhouse.updateIsExchanged(IsExchanged.RESERVED);
                reservedBookhouse.updateChatroomId(exchangeStatus.getChatroomId());
            }
            return new AcceptExchangeResponseDto(exchangeStatus.getRequestStatus(), true);
        }
        return new AcceptExchangeResponseDto(exchangeStatus.getRequestStatus(), false);
    }

    // 교환 요청 거절
    @Transactional
    public RequestStatus rejectExchangeStatus(Long memberId, Long exchangeRequestId) {

        ExchangeStatus exchangeStatus = exchangeStatusRepository.findById(exchangeRequestId)
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
        ExchangeStatus exchangeStatus = exchangeStatusRepository.findById(exchangeRequestId)
                .orElseThrow(BookhouseException.ExchangeStatusNotFound::new);

        Bookhouse bookhouse = bookhouseRepository.findById(exchangeStatus.getBookhouseId())
                .orElseThrow(BookhouseException.BookhouseNotFound::new);

        // 요청 대상 책의 소유자는 취소할 수 없음 (요청자만 취소 가능)
        if (bookhouse.getMemberId().equals(memberId)) {
            throw new BookhouseException.MemberIsNotBookOwner();
        }

        if (exchangeStatus.getRequestStatus() != RequestStatus.REQUEST) {
            throw new BookhouseException.InvalidExchangeTransitionCancel();
        }

        exchangeStatusRepository.delete(exchangeStatus);
    }

    // 교환 완료 - RESERVED -> EXCHANGED
    @Transactional
    public void completeExchange(Long chatroomId) {
        List<ExchangeStatus> exchangeStatusList = exchangeStatusRepository.findByChatroomId(chatroomId);
        
        if (exchangeStatusList.size() != 2) {
            throw new BookhouseException.InvalidExchangeStatusCount();
        }

        // 두 개의 Bookhouse를 모두 EXCHANGED로 변경
        for (ExchangeStatus status : exchangeStatusList) {
            Bookhouse bookhouse = bookhouseRepository.findById(status.getBookhouseId())
                    .orElseThrow(BookhouseException.BookhouseNotFound::new);
            
            if (bookhouse.getIsExchanged() != IsExchanged.RESERVED) {
                throw new BookhouseException.InvalidExchangeStatusForComplete();
            }
            
            bookhouse.updateIsExchanged(IsExchanged.EXCHANGED);
        }
    }

    // 교환 반납 - EXCHANGED -> PENDING
    @Transactional
    public void returnExchange(Long chatroomId) {
        List<ExchangeStatus> exchangeStatusList = exchangeStatusRepository.findByChatroomId(chatroomId);
        
        if (exchangeStatusList.size() != 2) {
            throw new BookhouseException.InvalidExchangeStatusCount();
        }

        // 두 개의 Bookhouse를 모두 PENDING으로 변경
        for (ExchangeStatus status : exchangeStatusList) {
            Bookhouse bookhouse = bookhouseRepository.findById(status.getBookhouseId())
                    .orElseThrow(BookhouseException.BookhouseNotFound::new);
            
            if (bookhouse.getIsExchanged() != IsExchanged.EXCHANGED) {
                throw new BookhouseException.InvalidExchangeStatusForReturn();
            }
            
            bookhouse.updateIsExchanged(IsExchanged.PENDING);
            bookhouse.updateChatroomId(null);  // 채팅방 연결 해제
        }
        
        // TODO ExchangeStatus 레코드 삭제 필요?
    }
}
