package kr.co.readingtown.chat.exception;

import kr.co.readingtown.common.exception.CustomException;

public class ChatException extends CustomException {

    public ChatException(final ChatErrorCode chatErrorCode) {
        super(chatErrorCode);
    }

    public static class ChatroomNotFound extends ChatException {
        public ChatroomNotFound() {
            super(ChatErrorCode.CHATROOM_NOT_FOUND);
        }
    }
}
