package kr.co.readingtown.member.dto;

import lombok.Data;

@Data
public class OnboardingRequestDto {
    private String phoneNumber;
    private String currentTown;
    private String username;
    private String profileImage;
    private String availableTime;
}
