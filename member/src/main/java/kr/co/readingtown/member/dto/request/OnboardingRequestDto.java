package kr.co.readingtown.member.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OnboardingRequestDto {

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phoneNumber;
    @NotBlank(message = "현재 동네는 필수입니다.")
    private String currentTown;
    @NotBlank(message = "닉네임은 필수입니다.")
    private String username;
    @NotBlank(message = "프로필 이미지는 필수입니다.")
    private String profileImage;
    private String availableTime;
}
