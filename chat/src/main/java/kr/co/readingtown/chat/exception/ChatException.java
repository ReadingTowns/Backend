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

    public static class MemberNotInChatroom extends ChatException {
        public MemberNotInChatroom() {
            super(ChatErrorCode.MEMBER_NOT_IN_CHATROOM);
        }
    }
    
    public static class CannotLeaveDuringExchange extends ChatException {
        public CannotLeaveDuringExchange() {
            super(ChatErrorCode.CANNOT_LEAVE_DURING_EXCHANGE);
        }
    }
    
    public static class CannotLeaveDuringReservation extends ChatException {
        public CannotLeaveDuringReservation() { super(ChatErrorCode.CANNOT_LEAVE_DURING_RESERVATION); }
    }

    // ChatBot 관련 예외
    public static class MessageSaveFailed extends ChatException {
        public MessageSaveFailed() {
            super(ChatErrorCode.CHATBOT_MESSAGE_SAVE_FAILED);
        }
    }

    public static class ApiCallFailed extends ChatException {
        public ApiCallFailed() {
            super(ChatErrorCode.CHATBOT_API_CALL_FAILED);
        }
    }

    public static class ResponseSaveFailed extends ChatException {
        public ResponseSaveFailed() {
            super(ChatErrorCode.CHATBOT_RESPONSE_SAVE_FAILED);
        }
    }
}
