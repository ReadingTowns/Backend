package kr.co.readingtown.chat.externalapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.readingtown.chat.dto.request.ChatRequestDto;
import kr.co.readingtown.chat.dto.response.*;
import kr.co.readingtown.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatrooms")
@Tag(name = "Chat API", description = "채팅 관련 API")
public class ExternalChatController {

    private final ChatService chatService;

    @PostMapping
    @Operation(summary = "채팅룸 생성", description = "서재 페이지에서 채팅 버튼을 눌러 채팅룸을 생성하는 API 입니다. 생성한 채팅룸 id를 반환합니다.")
    public ChatroomIdResponse createChatroom(
            @RequestBody ChatRequestDto chatRequestDto,
            @AuthenticationPrincipal Long memberId) {

        return chatService.createChatroom(chatRequestDto, memberId);
    }

    @GetMapping("/{chatroomId}/books")
    @Operation(summary = "교환 책 정보 조회", description = "채팅 페이지에서 교환 책 정보를 조회하는 API 입니다.")
    public ChatExchangedBookInfoResponse getExchangedBookInfo(@PathVariable Long chatroomId) {

        return chatService.getExchangedBookInfo(chatroomId);
    }

    @GetMapping("/{chatroomId}/partner/profile")
    @Operation(summary = "채팅 상대방 정보 조회", description = "채팅 상대방 정보(id, 닉네임, 프로필 사진)를 조회하는 API 입니다.")
    public ChatMemberInfoResponse getPartnerInfo(
            @PathVariable Long chatroomId,
            @AuthenticationPrincipal Long myId
    ) {
        return chatService.getPartnerInfo(chatroomId, myId);
    }

    @GetMapping("/{chatroomId}/messages")
    @Operation(summary = "이전 채팅 메시지 조회", description = "이전 채팅 메시지를 조회하는 API 입니다. 커서 기반 페이지네이션을 적용합니다.")
    public MessageListResponseDto getChatMessage(
            @PathVariable Long chatroomId,

            @Parameter(description = "가져올 메시지 개수 (ex.50)")
            @RequestParam int limit,

            @Parameter(description = "이 커서(메시지 ID) 이전의 메시지를 조회")
            @RequestParam Long before,

            @AuthenticationPrincipal Long myId) {

        return chatService.getChatMessage(chatroomId, limit, before, myId);
    }

    @GetMapping
    @Operation(summary = "채팅룸 리스트 조회", description = "로그인 한 유저가 속한 채팅룸 리스트를 조회하는 API 입니다.")
    public List<ChatroomPreviewResponseDto> getMyChatroom(@AuthenticationPrincipal Long myId) {

        return chatService.getMyChatroom(myId);
    }

    @DeleteMapping("/{chatroomId}")
    @Operation(summary = "채팅룸 나가기", description = "특정 채팅룸을 나가는 API 입니다.")
    public void leaveChatroom(
            @PathVariable Long chatroomId,
            @AuthenticationPrincipal Long myId
    ) {

        chatService.leaveChatroom(chatroomId, myId);
    }

}
