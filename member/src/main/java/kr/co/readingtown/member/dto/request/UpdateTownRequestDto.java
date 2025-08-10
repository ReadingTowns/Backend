package kr.co.readingtown.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UpdateTownRequestDto {
    @NotBlank
    private BigDecimal longitude;  // 경도 (x)
    @NotBlank
    private BigDecimal latitude;   // 위도 (y)
}
