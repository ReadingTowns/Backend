package kr.co.readingtown.bookhouse.internalapi;

import kr.co.readingtown.bookhouse.dto.response.ExchangeStatusResponse;
import kr.co.readingtown.bookhouse.service.BookhouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InternalBookhouseController {

    private final BookhouseService bookhouseService;

    @GetMapping("/internal/bookhouse/books")
    public List<Long> getMembersBookId(@RequestParam("memberId") Long memberId) {

        return bookhouseService.getMembersBookId(memberId);
    }

    @GetMapping("/internal/bookhouse/{chatroomId}/status")
    public ExchangeStatusResponse getExchangeStatus(@PathVariable Long chatroomId) {

        return bookhouseService.getExchangeStatusForChatroom(chatroomId);
    }
}
