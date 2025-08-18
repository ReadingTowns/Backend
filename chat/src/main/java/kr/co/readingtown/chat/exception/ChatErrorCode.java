package kr.co.readingtown.chat.exception;

import kr.co.readingtown.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatErrorCode implements ErrorCode {

    CHATROOM_NOT_FOUND(9001, "해당 채팅룸은 존재하지 않습니다.");

    private final int errorCode;
    private final String errorMessage;
}
