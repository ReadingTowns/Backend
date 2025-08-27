package kr.co.readingtown.bookhouse.exception;

import kr.co.readingtown.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookhouseErrorCode implements ErrorCode {

    BOOKHOUSE_NOT_FOUND(7001, "서재에 해당 책에 대한 정보가 존재하지 않습니다."),
    BOOKHOUSE_ALREADY_EXISTS(7002, "서재에 해당 책이 이미 존재합니다."),

    //교환 요청
    EXCHANGE_NOT_FOUND(7011, "해당 교환 요청에 대한 정보가 존재하지 않습니다."),
    INVALID_EXCHANGE_TRANSITION_CANCEL(7012, "요청 상태에서만 취소 가능합니다."),
    INVALID_EXCHANGE_TRANSITION_REJECT(7012, "요청 상태에서만 거절 가능합니다."),
    DOMAIN_INVARIANT_BROKEN(7013, "맞교환할 서재 수가 2건이 아닙니다."),
    INVALID_EXCHANGE_STATUS_FOR_TRANSITION(7014, "교환 요청 상태가 아닙니다."),
    FORBIDDEN_ACCEPT_REQUEST(7015, "로그인된 멤버와 해당 교환 요청을 받은 멤버와 달라 권한이 없습니다."),
    DUPLICATE_EXCHANGE_REQUEST(7016, "이미 해당 채팅방에 해당 책에 대해 교환 요청이 생성되어 있습니다."),
    MEMBER_IS_NOT_BOOK_OWNER(7017,"책의 소유자가 아닌 요청자만 요청 취소가 가능합니다." ),

    //대면 교환
    INVALID_EXCHANGE_STATUS_COUNT(7021, "채팅 방 내 생성된 교환 요청 개수가 2개가 아닙니다."),
    INVALID_EXCHANGE_STATUS_FOR_COMPLETE(7022, "책 교환 상태가 'RESERVED'이어야 대면 교환이 가능합니다."),
    INVALID_EXCHANGE_STATUS_FOR_RETURN(7023, "책이 'EXCHANGED'인 상태가 아니라 반납이 불가합니다.");


    private final int errorCode;
    private final String errorMessage;
}
