package kr.co.readingtown.member.exception;

import kr.co.readingtown.common.exception.CustomException;

public class MemberException extends CustomException {
    public MemberException(final MemberErrorCode memberErrorCode) {
        super(memberErrorCode);
    }
    public static class MissingRequiredField extends MemberException {
        public MissingRequiredField() {
            super(MemberErrorCode.MISSING_REQUIRED_FIELD);
        }
    }
    public static class NoAuthMember extends MemberException {
        public NoAuthMember() {
            super(MemberErrorCode.NO_AUTH_MEMBER);
        }
    }

    public static class UsernameGenerationFailed extends MemberException {
        public UsernameGenerationFailed() {
            super(MemberErrorCode.USERNAME_GENERATION_FAILED);
        }
    }

    public static class InvalidUsername extends MemberException {
        public InvalidUsername() {
            super(MemberErrorCode.INVALID_USERNAME_FORMAT);
        }
    }

    public static class UsernameAlreadyExists extends MemberException {
        public UsernameAlreadyExists() {
            super(MemberErrorCode.USERNAME_ALREADY_EXISTS);
        }
    }

    public static class SelfRatingNotAllowed extends MemberException {
        public SelfRatingNotAllowed() {
            super(MemberErrorCode.SELF_RATING_NOT_ALLOWED);
        }
    }
}
