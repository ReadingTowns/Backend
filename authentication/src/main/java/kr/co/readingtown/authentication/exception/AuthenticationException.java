package kr.co.readingtown.authentication.exception;

import kr.co.readingtown.common.exception.CustomException;

public class AuthenticationException extends CustomException {

    public AuthenticationException(final AuthenticationErrorCode authenticationErrorCode) {
        super(authenticationErrorCode);
    }

    public static class ExpiredToken extends AuthenticationException {
        public ExpiredToken() {
            super(AuthenticationErrorCode.EXPIRED_TOKEN);
        }
    }

    public static class TokenNotValid extends AuthenticationException {
        public TokenNotValid() {
            super(AuthenticationErrorCode.TOKEN_NOT_VALID);
        }
    }

    public static class NoTokenException extends AuthenticationException {
        public NoTokenException() {
            super(AuthenticationErrorCode.NO_TOKEN_EXCEPTION);
        }
    }
}
