package kr.co.readingtown.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequestDto(
    @NotBlank
    String nickname,

    @NotBlank
    String profileImage,

    String availableTime
) {
}
