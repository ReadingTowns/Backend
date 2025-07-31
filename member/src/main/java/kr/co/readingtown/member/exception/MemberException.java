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
}
