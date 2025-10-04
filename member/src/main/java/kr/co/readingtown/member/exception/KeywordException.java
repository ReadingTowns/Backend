package kr.co.readingtown.member.exception;

import kr.co.readingtown.common.exception.CustomException;

public class KeywordException extends CustomException {

    public KeywordException(final KeywordErrorCode keywordErrorCode) {

        super(keywordErrorCode);
    }

    public static class NotFoundKeyword extends KeywordException {

        public NotFoundKeyword() {

            super(KeywordErrorCode.NOT_FOUND_KEYWORD);
        }
    }
}
