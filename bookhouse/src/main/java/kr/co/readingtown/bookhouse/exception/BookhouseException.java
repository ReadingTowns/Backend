package kr.co.readingtown.bookhouse.exception;

import kr.co.readingtown.common.exception.CustomException;

public class BookhouseException extends CustomException {

    public BookhouseException(final BookhouseErrorCode bookhouseErrorCode) {
        super(bookhouseErrorCode);
    }

    public static class BookhouseNotFound extends BookhouseException {
        public BookhouseNotFound() {
            super(BookhouseErrorCode.BOOKHOUSE_NOT_FOUND);
        }
    }

    public static class BookhouseAlreadyExists extends BookhouseException {
        public BookhouseAlreadyExists() {
            super(BookhouseErrorCode.BOOKHOUSE_ALREADY_EXISTS);
        }
    }

    public static class ExchangeStatusNotFound extends BookhouseException {
        public ExchangeStatusNotFound() {
            super(BookhouseErrorCode.EXCHANGE_NOT_FOUND);
        }
    }

    public static class InvalidExchangeTransitionCancel extends BookhouseException {
        public InvalidExchangeTransitionCancel() { super(BookhouseErrorCode.INVALID_EXCHANGE_TRANSITION_CANCEL); }
    }

    public static class InvalidExchangeTransitionReject extends BookhouseException {
        public InvalidExchangeTransitionReject() {super(BookhouseErrorCode.INVALID_EXCHANGE_TRANSITION_REJECT);}
    }

    public static class DomainInvariantBroken extends BookhouseException {
        public DomainInvariantBroken() { super(BookhouseErrorCode.DOMAIN_INVARIANT_BROKEN);}
    }

    public static class InvalidExchangeTransitionAlreadyRejected extends BookhouseException {
        public InvalidExchangeTransitionAlreadyRejected() { super(BookhouseErrorCode.INVALID_EXCHANGE_TRANSITION_ALREADY_REJECTED);}
    }
}
