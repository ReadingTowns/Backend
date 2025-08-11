package kr.co.readingtown.bookhouse.domain;

public enum IsExchanged {
    PENDING, //반납 완료 또는 취소
    RESERVED, //예약 완료
    EXCHANGED //교환 중
}
