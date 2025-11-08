package kr.co.readingtown.chat.exception;

import kr.co.readingtown.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatErrorCode implements ErrorCode {

    CHATROOM_NOT_FOUND(9001, "해당 채팅룸은 존재하지 않습니다."),
    MEMBER_NOT_IN_CHATROOM(9002, "해당 멤버는 채팅룸에 속해있지 않습니다."),
    CANNOT_LEAVE_DURING_EXCHANGE(9003, "교환 중에는 채팅방을 나갈 수 없습니다."),
    CANNOT_LEAVE_DURING_RESERVATION(9004, "예약 중에는 채팅방을 나갈 수 없습니다."),

    // ChatBot 관련 에러
    CHATBOT_MESSAGE_SAVE_FAILED(9101, "메시지 저장에 실패했습니다. 잠시 후 다시 시도해주세요."),
    CHATBOT_API_CALL_FAILED(9102, "AI 응답 생성에 실패했습니다. 잠시 후 다시 시도해주세요."),
    CHATBOT_RESPONSE_SAVE_FAILED(9103, "응답 저장에 실패했습니다. 잠시 후 다시 시도해주세요.");

    private final int errorCode;
    private final String errorMessage;
}
