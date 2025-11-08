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
import kr.co.readingtown.bookhouse.integration.chat.ChatClient;
import kr.co.readingtown.bookhouse.dto.request.internal.ExchangeRequestMessageDto;
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
    private final ChatClient chatClient;
    private final BookhouseService bookhouseService;

    // 채팅방 내 교환요청한 책 정보 조회
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

        ExchangeStatus exchangeStatus = exchangeStatusRepository.save(newExchangeStatus);

        // ✅ 채팅방 ID 찾아서 자동 메시지 전송
        ExchangeRequestMessageDto messageDto = new ExchangeRequestMessageDto(
                exchangeStatus.getChatroomId(),
                memberId,  // 교환 요청자 ID
                "교환 신청이 도착했습니다.",
                "EXCHANGE_REQUEST",  // MessageType.EXCHANGE_REQUEST as String
                exchangeStatus.getExchangeStatusId()
        );
        chatClient.sendSystemMessage(messageDto);

        return new ExchangeResponseDto(exchangeStatus.getExchangeStatusId(), newExchangeStatus.getRequestStatus());
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
            
            // ✅ 예약 완료 시스템 메시지 전송
            try {
                ExchangeRequestMessageDto messageDto = new ExchangeRequestMessageDto(
                        exchangeStatus.getChatroomId(),
                        0L,  // 시스템 메시지 (양측 모두 수락한 경우)
                        "교환이 예약되었습니다. 대면 교환을 진행해주세요.",
                        "EXCHANGE_RESERVED",
                        exchangeStatus.getExchangeStatusId()
                );
                chatClient.sendSystemMessage(messageDto);
            } catch (Exception e) {
                // 채팅 서비스 장애 시에도 교환 수락은 정상 처리
            }
        } else {
            // ✅ 교환 수락 시스템 메시지 전송 (아직 한 명만 수락한 경우)
            try {
                ExchangeRequestMessageDto messageDto = new ExchangeRequestMessageDto(
                        exchangeStatus.getChatroomId(),
                        memberId,  // 수락한 사람의 ID
                        "교환 신청이 수락되었습니다.",
                        "EXCHANGE_ACCEPTED",
                        exchangeStatus.getExchangeStatusId()
                );
                chatClient.sendSystemMessage(messageDto);
            } catch (Exception e) {
                // 채팅 서비스 장애 시에도 교환 수락은 정상 처리
            }
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

        // ✅ 교환 거절 시스템 메시지 전송
        try {
            ExchangeRequestMessageDto messageDto = new ExchangeRequestMessageDto(
                    exchangeStatus.getChatroomId(),
                    memberId,  // 거절한 사람의 ID
                    "교환 신청이 거절되었습니다.",
                    "EXCHANGE_REJECTED",
                    exchangeStatus.getExchangeStatusId()
            );
            chatClient.sendSystemMessage(messageDto);
        } catch (Exception e) {
            // 채팅 서비스 장애 시에도 교환 거절은 정상 처리
        }
        
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

        // ✅ 교환 취소 시스템 메시지 전송 (삭제 전에 전송)
        try {
            ExchangeRequestMessageDto messageDto = new ExchangeRequestMessageDto(
                    exchangeStatus.getChatroomId(),
                    memberId,  // 취소한 사람의 ID
                    "교환 신청이 취소되었습니다.",
                    "EXCHANGE_CANCELED",  // 교환 취소 메시지 타입
                    exchangeStatus.getExchangeStatusId()
            );
            chatClient.sendSystemMessage(messageDto);
        } catch (Exception e) {
            // 채팅 서비스 장애 시에도 교환 취소는 정상 처리
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

        // ✅ 교환 완료 시스템 메시지 전송
        try {
            ExchangeRequestMessageDto messageDto = new ExchangeRequestMessageDto(
                    chatroomId,
                    0L,  // 시스템 메시지 (양측 모두 완료)
                    "교환이 완료되었습니다. 즐거운 독서 되세요!",
                    "EXCHANGE_COMPLETED",
                    null  // 특정 교환 상태가 아닌 전체 교환 완료
            );
            chatClient.sendSystemMessage(messageDto);
        } catch (Exception e) {
            // 채팅 서비스 장애 시에도 교환 완료는 정상 처리
        }
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

        // ✅ 교환 반납 시스템 메시지 전송
        try {
            ExchangeRequestMessageDto messageDto = new ExchangeRequestMessageDto(
                    chatroomId,
                    0L,  // 시스템 메시지 (양측 모두 반납)
                    "교환이 반납되었습니다. 감사합니다.",
                    "EXCHANGE_RETURNED",
                    null  // 특정 교환 상태가 아닌 전체 반납
            );
            chatClient.sendSystemMessage(messageDto);
        } catch (Exception e) {
            // 채팅 서비스 장애 시에도 교환 반납은 정상 처리
        }
        
        // TODO ExchangeStatus 레코드 삭제 필요할지
    }

    public kr.co.readingtown.bookhouse.dto.response.ExchangeStatusResponse getExchangeStatus(Long chatroomId) {
        List<ExchangeStatus> exchangeStatusList = exchangeStatusRepository.findByChatroomId(chatroomId);

        if (exchangeStatusList.isEmpty()) {
            return null;
        }

        // 첫 번째 책의 IsExchanged 상태 확인 (두 책은 항상 동일한 상태여야 함)
        Long bookhouseId = exchangeStatusList.get(0).getBookhouseId();
        Bookhouse bookhouse = bookhouseRepository.findById(bookhouseId)
                .orElseThrow(BookhouseException.BookhouseNotFound::new);

        // IsExchanged enum을 String으로 변환하여 반환
        return new kr.co.readingtown.bookhouse.dto.response.ExchangeStatusResponse(
                bookhouse.getIsExchanged().name()
        );
    }
}
