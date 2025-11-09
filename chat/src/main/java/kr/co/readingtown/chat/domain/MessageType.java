package kr.co.readingtown.chat.domain;

public enum MessageType {
    TEXT,               // 일반 메시지
    EXCHANGE_REQUEST,   // 교환 신청
    EXCHANGE_ACCEPTED,  // 교환 수락
    EXCHANGE_REJECTED,  // 교환 거절
    EXCHANGE_CANCELED,  // 교환 취소
    EXCHANGE_RESERVED,  // 예약 완료
    EXCHANGE_COMPLETED, // 교환 완료
    EXCHANGE_RETURNED,  // 반납 완료
    SYSTEM,             // 시스템 자동 메시지
    PING                // Heartbeat (연결 유지용)
}
