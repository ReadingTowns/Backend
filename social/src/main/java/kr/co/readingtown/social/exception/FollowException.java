package kr.co.readingtown.social.exception;

import kr.co.readingtown.common.exception.CustomException;

public class FollowException extends CustomException {

    public FollowException(final FollowErrorCode followErrorCode) {
        super(followErrorCode);
    }

    public static class selfFollowNotAllowed extends FollowException {
        public selfFollowNotAllowed() {
            super(FollowErrorCode.SELF_FOLLOW_NOT_ALLOWED);
        }
    }
}
