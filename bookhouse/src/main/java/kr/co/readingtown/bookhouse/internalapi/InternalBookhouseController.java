package kr.co.readingtown.bookhouse.internalapi;

import kr.co.readingtown.bookhouse.service.BookhouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/bookhouses")
public class InternalBookhouseController {

    private final BookhouseService bookhouseService;

    @PostMapping
    public void registerBookhouse(@RequestParam("memberId") Long memberId) {
        bookhouseService.registerBookhouse(memberId);
    }

    @GetMapping("/id")
    public Long getBookhouseIdByMember(@RequestParam("memberId") Long memberId) {
        return bookhouseService.getBookhouseId(memberId);
    }
}
