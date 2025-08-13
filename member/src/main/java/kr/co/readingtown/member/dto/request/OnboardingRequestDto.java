package kr.co.readingtown.member.dto.request;


import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OnboardingRequestDto {

    @NotBlank(message = "전화번호는 필수입니다.") //Todo 패턴 정하기
    private String phoneNumber;

    @NotNull(message = "현재 경도는 필수입니다.")
    @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
    @DecimalMax(value = "180.0",  message = "경도는 180 이하이어야 합니다.")
    private BigDecimal longitude;

    @NotNull(message = "현재 위도는 필수입니다.")
    @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
    @DecimalMax(value = "90.0",  message = "위도는 90 이하이어야 합니다.")
    private BigDecimal latitude;

    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @NotBlank(message = "프로필 이미지는 필수입니다.")
    private String profileImage;

    private String availableTime;
}
