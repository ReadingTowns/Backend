package kr.co.readingtown.member.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FollowBulkCheckRequestDto {
    @NotNull
    private Long fromMemberId;
    @NotEmpty
    private List<Long> toMemberIds;

    @Builder
    public FollowBulkCheckRequestDto(Long fromMemberId, List<Long> toMemberIds) {
        this.fromMemberId = fromMemberId;
        this.toMemberIds = toMemberIds;
    }
}