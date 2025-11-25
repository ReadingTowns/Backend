package kr.co.readingtown.bookhouse.internalapi;

import kr.co.readingtown.bookhouse.dto.response.ExchangeStatusResponse;
import kr.co.readingtown.bookhouse.service.BookhouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @DeleteMapping("/internal/bookhouse/revoke")
    public void deleteMembersBookhouse(@RequestParam("memberId") Long memberId) {

        bookhouseService.deleteMembersBookhouse(memberId);
    }

    @GetMapping("/internal/bookhouse/{chatroomId}/status")
    public ExchangeStatusResponse getIsExchanged(@PathVariable Long chatroomId) {

        return bookhouseService.getIsExchangedForChatroom(chatroomId);
    }
}
