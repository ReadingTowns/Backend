package kr.co.readingtown.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UpdateProfileRequestDto {
    @NotBlank
    private String username;

    @NotBlank
    private String profileImage;

    private String availableTime;
}
