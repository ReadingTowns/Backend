package kr.co.readingtown.bookhouse.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseEntity;
import kr.co.readingtown.bookhouse.domain.enums.RequestStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "exchange_status")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExchangeStatus extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exchange_status_id")
    private Long exchangeStatusId;

    @Column(name = "chatroom_id")
    private Long chatroomId;

    @Column(name = "bookhouse_id")
    private Long bookhouseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private RequestStatus requestStatus;

    @Builder
    public ExchangeStatus(Long chatroomId, Long bookhouseId, RequestStatus requestStatus) {
        this.chatroomId = chatroomId;
        this.bookhouseId = bookhouseId;
        this.requestStatus = requestStatus;
    }
}
