package kr.co.readingtown.chat.externalapi;

import kr.co.readingtown.chat.dto.request.ChatRequestDto;
import kr.co.readingtown.chat.dto.response.ChatExchangedBookInfoResponse;
import kr.co.readingtown.chat.dto.response.ChatMemberInfoResponse;
import kr.co.readingtown.chat.dto.response.ChatroomIdResponse;
import kr.co.readingtown.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatrooms")
public class ExternalChatController {

    private final ChatService chatService;

    @PostMapping
    public ChatroomIdResponse createChatroom(
            @RequestBody ChatRequestDto chatRequestDto,
            @AuthenticationPrincipal Long memberId) {

        return chatService.createChatroom(chatRequestDto, memberId);
    }

    @GetMapping("/{chatroomId}/books")
    public ChatExchangedBookInfoResponse getExchangedBookInfo(@PathVariable Long chatroomId) {

        return chatService.getExchangedBookInfo(chatroomId);
    }

    @GetMapping("/{chatroomId}/partner/profile")
    public ChatMemberInfoResponse getPartnerInfo(
            @PathVariable Long chatroomId,
            @AuthenticationPrincipal Long myId
    ) {
        return chatService.getPartnerInfo(chatroomId, myId);
    }
}
