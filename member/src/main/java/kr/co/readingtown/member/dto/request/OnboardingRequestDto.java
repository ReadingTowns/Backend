package kr.co.readingtown.member.dto.request;


import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phoneNumber;
    @NotBlank(message = "현재 경도는 필수입니다.")
    private BigDecimal longitude;
    @NotBlank(message = "현재 위도는 필수입니다.")
    private BigDecimal latitude;
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;
    @NotBlank(message = "프로필 이미지는 필수입니다.")
    private String profileImage;
    private String availableTime;
}
