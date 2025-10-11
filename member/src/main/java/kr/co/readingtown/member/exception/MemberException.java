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

    public static class NotFoundMember extends MemberException {
        public NotFoundMember() {
            super(MemberErrorCode.NOT_FOUND_MEMBER);
        }
    }

    public static class NicknameGenerationFailed extends MemberException {
        public NicknameGenerationFailed() {
            super(MemberErrorCode.NICKNAME_GENERATION_FAILED);
        }
    }

    public static class InvalidNickname extends MemberException {
        public InvalidNickname() {
            super(MemberErrorCode.INVALID_NICKNAME_FORMAT);
        }
    }

    public static class NicknameAlreadyExists extends MemberException {
        public NicknameAlreadyExists() {
            super(MemberErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    public static class SelfRatingNotAllowed extends MemberException {
        public SelfRatingNotAllowed() {
            super(MemberErrorCode.SELF_RATING_NOT_ALLOWED);
        }
    }

    public static class TownResolvedFailed extends MemberException {
        public TownResolvedFailed() { super(MemberErrorCode.TOWN_RESOLVED_FAILED); }
    }
}
