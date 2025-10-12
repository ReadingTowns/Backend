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
}
