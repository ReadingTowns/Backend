package kr.co.readingtown.chat.externalapi;

import kr.co.readingtown.chat.dto.request.ChatRequestDto;
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
    public void createChatroom(
            @RequestBody ChatRequestDto chatRequestDto,
            @AuthenticationPrincipal Long memberId) {


    }
}
