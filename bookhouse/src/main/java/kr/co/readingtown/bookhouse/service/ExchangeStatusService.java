package kr.co.readingtown.bookhouse.service;

import kr.co.readingtown.bookhouse.domain.Bookhouse;
import kr.co.readingtown.bookhouse.domain.ExchangeStatus;
import kr.co.readingtown.bookhouse.domain.enums.IsExchanged;
import kr.co.readingtown.bookhouse.domain.enums.RequestStatus;
import kr.co.readingtown.bookhouse.dto.request.ChatRequestDto;
import kr.co.readingtown.bookhouse.dto.response.ExchangedBookResponse;
import kr.co.readingtown.bookhouse.dto.response.ExchangedDetail;
import kr.co.readingtown.bookhouse.exception.BookhouseException;
import kr.co.readingtown.bookhouse.repository.BookhouseRepository;
import kr.co.readingtown.bookhouse.repository.ExchangeStatusRepository;
import kr.co.readingtown.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExchangeStatusService {

    private final BookhouseRepository bookhouseRepository;
    private final ExchangeStatusRepository exchangeStatusRepository;

    @Transactional
    public void createExchangeStatus(ChatRequestDto chatRequestDto) {

        Bookhouse bookhouse = bookhouseRepository.findByMemberIdAndBookId(chatRequestDto.memberId(), chatRequestDto.bookId())
                .orElseThrow(BookhouseException.BookhouseNotFound::new);

        ExchangeStatus newExchangeStatus = ExchangeStatus.builder()
                .chatroomId(chatRequestDto.chatroomId())
                .bookhouseId(bookhouse.getBookhouseId())
                .requestStatus(RequestStatus.REQUEST)
                .build();
        exchangeStatusRepository.save(newExchangeStatus);
        //Todo responsebody 생성된 교환id, 현재 RequestStatus 상태 반환해주기
    }

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

    //Todo MemberId가 해당 exchangeRequestId와 관련있는 사람인지 확인.
    @Transactional
    public RequestStatus acceptExchangeStatus(Long memberId, Long exchangeRequestId) {
        ExchangeStatus exchangeStatus = exchangeStatusRepository.findById(exchangeRequestId)
                .orElseThrow(BookhouseException.ExchangeStatusNotFound::new);

        // (선택) memberId 가 해당 bookhouse 의 소유자인지 검증 가능 ForbiddenAcceptRequest();

        if (exchangeStatus.getRequestStatus() == RequestStatus.REJECTED) {
            throw new BookhouseException.InvalidExchangeTransitionAlreadyRejected();
        }

        exchangeStatus.updateRequestStatus(RequestStatus.ACCEPTED);

        // 같은 채팅방 내 ACCEPTED 개수 확인
        List<ExchangeStatus> acceptedInRoom =
                exchangeStatusRepository.findAllAcceptedByChatRoomId(exchangeStatus.getChatroomId(), RequestStatus.ACCEPTED);

        if (acceptedInRoom.size() >= 2) {

            if (acceptedInRoom.size() != 2) {
                throw new BookhouseException.DomainInvariantBroken();
            }

            // 두 건의 bookhouse 를 예약(RESERVED) 상태로 전환
            List<Long> bookhouseIds = acceptedInRoom.stream()
                    .map(ExchangeStatus::getBookhouseId)
                    .toList();

            List<Bookhouse> bookhouses = bookhouseRepository.findAllById(bookhouseIds);

            // 해당 채팅방으로 예약 상태 설정
            for (Bookhouse bookhouse : bookhouses) {
                bookhouse.updateIsExchanged(IsExchanged.RESERVED);
                bookhouse.updateChatroomId(exchangeStatus.getChatroomId());
            }
        }
        return exchangeStatus.getRequestStatus();
    }

    @Transactional
    public RequestStatus rejectExchangeStatus(Long memberId, Long exchangeRequestId) {
        ExchangeStatus exchangeStatus = exchangeStatusRepository.findById(exchangeRequestId)
                .orElseThrow(BookhouseException.ExchangeStatusNotFound::new);

        if (exchangeStatus.getRequestStatus() != RequestStatus.REQUEST) {
            throw new BookhouseException.InvalidExchangeTransitionReject();
        }

        exchangeStatus.updateRequestStatus(RequestStatus.REJECTED);
        return exchangeStatus.getRequestStatus();
    }


    @Transactional
    public RequestStatus cancelExchangeStatus(Long memberId, Long exchangeRequestId) {
        ExchangeStatus exchangeStatus = exchangeStatusRepository.findById(exchangeRequestId)
                .orElseThrow(BookhouseException.ExchangeStatusNotFound::new);

        if (exchangeStatus.getRequestStatus() != RequestStatus.REQUEST) {
            throw new BookhouseException.InvalidExchangeTransitionCancel();
        }

        exchangeStatus.updateRequestStatus(RequestStatus.PENDING);
        return exchangeStatus.getRequestStatus();
    }
}
