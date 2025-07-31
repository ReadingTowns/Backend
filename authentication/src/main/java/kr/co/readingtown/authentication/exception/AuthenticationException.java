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

    public static class TokenCategoryMismatch extends AuthenticationException {
        public TokenCategoryMismatch() {
            super(AuthenticationErrorCode.TOKEN_CATEGORY_MISMATCH);
        }
    }

    public static class InvalidRefreshToken extends AuthenticationException {
        public InvalidRefreshToken() {
            super(AuthenticationErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    public static class RefreshTokenNotFount extends AuthenticationException {
        public RefreshTokenNotFount() {
            super(AuthenticationErrorCode.REFRESH_TOKEN_NOT_FOUNT);
        }
    }

    public static class BlacklistedToken extends AuthenticationException {
        public BlacklistedToken() {
            super(AuthenticationErrorCode.BLACKLISTED_TOKEN);
        }
    }
}
