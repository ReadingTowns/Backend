package kr.co.readingtown.bookhouse.externalapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.readingtown.bookhouse.dto.request.BookInfoRequestDto;
import kr.co.readingtown.bookhouse.dto.response.BookPreviewResponseDto;
import kr.co.readingtown.bookhouse.service.BookhouseService;
import kr.co.readingtown.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookhouse")
@Tag(name = "Bookhouse API", description = "서재 관련 API")
public class ExternalBookhouseController {

    private final BookhouseService bookhouseService;

    @PostMapping("/books")
    @Operation(summary = "서재에 책 추가", description = "현재 로그인한 회원의 서재에 특정 책을 추가합니다.")
    public void addBooksToBookhouse(
            @AuthenticationPrincipal Long memberId,
            @RequestBody BookInfoRequestDto bookInfoRequestDto) {

        bookhouseService.addBooksToBookhouse(memberId, bookInfoRequestDto);
    }

    @DeleteMapping("/books/{bookId}")
    @Operation(summary = "서재에서 책 삭제", description = "현재 로그인한 회원의 서재에서 특정 책을 삭제합니다.")
    public void deleteBookFromBookhouse(
            @PathVariable Long bookId,
            @AuthenticationPrincipal Long memberId) {

        bookhouseService.deleteBookFromBookhouse(memberId, bookId);
    }

    @GetMapping("/members/me")
    @Operation(summary = "내 서재 조회", description = "현재 로그인한 회원의 서재를 조회합니다.")
    public PageResponse<BookPreviewResponseDto> getMyBookhouse(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return bookhouseService.getBookhouseBooks(memberId, page, size);
    }

    @GetMapping("/members/{memberId}")
    @Operation(summary = "회원의 서재 조회", description = "특정 회원의 서재를 조회합니다.")
    public PageResponse<BookPreviewResponseDto> getBookhouseByMemberId(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return bookhouseService.getBookhouseBooks(memberId, page, size);
    }

}
