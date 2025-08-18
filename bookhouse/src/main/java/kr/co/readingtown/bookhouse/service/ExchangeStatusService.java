package kr.co.readingtown.bookhouse.service;

import kr.co.readingtown.bookhouse.domain.Bookhouse;
import kr.co.readingtown.bookhouse.domain.ExchangeStatus;
import kr.co.readingtown.bookhouse.domain.enums.RequestStatus;
import kr.co.readingtown.bookhouse.dto.request.ChatRequestDto;
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
}
