package kr.co.readingtown.chat.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import kr.co.readingtown.chat.dto.request.ChatMessageRequestDto;
import kr.co.readingtown.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebsocketHandler extends TextWebSocketHandler {

    // 채팅방 ID에 속한 세션을 알기 위한
    private final Map<String, Set<WebSocketSession>> chatRooms = new ConcurrentHashMap<>();

    // 해당 세션에 어떤 채팅방에 속해있는지 알기위한
    private final Map<WebSocketSession, String> sessionRoomMap = new ConcurrentHashMap<>();

    private final ChatService chatService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        // ✅ ChatService에 주입
        chatService.setChatRooms(chatRooms);
    }

    // 소켓 연결 확인
    // 프론트에서 /ws/conn으로 연결 요청하면
    // 1. HTTP -> WebSocket 핸드쉐이크 요청
    // 2. 연결 성공시 afterConnectionEstablished(WebSocketSession session) 호출
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        log.info("{} 연결됨", session.getId());
    }

    // 소켓 메시지 처리
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String payload = message.getPayload();
        log.info("payload {}", payload);

        // ✅ 1. JSON → ChatMessageRequestDto로 파싱
        ChatMessageRequestDto request = objectMapper.readValue(payload, ChatMessageRequestDto.class);

        String roomId = String.valueOf(request.chatroomId());

        // ✅ 2. 방 등록 및 세션 추가
        chatRooms.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
        sessionRoomMap.put(session, roomId);

        // ✅ 3. 인증된 사용자 확인
        Long senderId = (Long) session.getAttributes().get("memberId");
        if (senderId == null) {
            log.warn("인증되지 않은 사용자");
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        // ✅ 4. 메시지 저장 및 브로드캐스트
        chatService.saveAndBroadcastMessage(request);
    }

    // 소켓 연결 종료
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        log.info("{} 연결 끊김", session.getId());

        String roomId = sessionRoomMap.get(session);
        if (roomId != null) {
            Set<WebSocketSession> sessions = chatRooms.get(roomId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    chatRooms.remove(roomId);
                }
            }
        }
        sessionRoomMap.remove(session);
    }
}
