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

    public static class InvalidExchangeStatusForTransition extends BookhouseException {
        public InvalidExchangeStatusForTransition() { super(BookhouseErrorCode.INVALID_EXCHANGE_STATUS_FOR_TRANSITION);}
    }

    public static class ForbiddenAcceptRequest extends BookhouseException {
        public ForbiddenAcceptRequest() { super(BookhouseErrorCode.FORBIDDEN_ACCEPT_REQUEST);}
    }

    public static class DuplicateExchangeRequest extends BookhouseException {
        public DuplicateExchangeRequest() { super(BookhouseErrorCode.DUPLICATE_EXCHANGE_REQUEST);}
    }

    public static class ForbiddenCancelByBookOwner extends BookhouseException {
        public ForbiddenCancelByBookOwner() { super(BookhouseErrorCode.FORBIDDEN_CANCEL_BY_BOOK_OWNER);}
    }

    public static class InvalidExchangeStatusCount extends BookhouseException {
        public InvalidExchangeStatusCount() { super(BookhouseErrorCode.INVALID_EXCHANGE_STATUS_COUNT);}
    }

    public static class InvalidExchangeStatusForComplete extends BookhouseException {
        public InvalidExchangeStatusForComplete() { super(BookhouseErrorCode.INVALID_EXCHANGE_STATUS_FOR_COMPLETE);}
    }

    public static class InvalidExchangeStatusForReturn extends BookhouseException {
        public InvalidExchangeStatusForReturn() { super(BookhouseErrorCode.INVALID_EXCHANGE_STATUS_FOR_RETURN);}
    }
}
