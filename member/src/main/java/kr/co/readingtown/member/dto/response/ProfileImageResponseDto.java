package kr.co.readingtown.member.dto.response;

public record ProfileImageResponseDto(
        String uploadUrl
) {

    public static ProfileImageResponseDto of(String url) {
        return new ProfileImageResponseDto(url);
    }
}
