package kr.co.readingtown.member.dto.response;

public record ProfileResponseDto(
    Long memberId,
    String profileImage,
    String nickname,
    String currentTown,
    Double userRating,
    int userRatingCount,
    String availableTime
) {
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Long memberId;
        private String profileImage;
        private String nickname;
        private String currentTown;
        private Double userRating;
        private int userRatingCount;
        private String availableTime;
        
        public Builder memberId(Long memberId) {
            this.memberId = memberId;
            return this;
        }
        
        public Builder profileImage(String profileImage) {
            this.profileImage = profileImage;
            return this;
        }
        
        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }
        
        public Builder currentTown(String currentTown) {
            this.currentTown = currentTown;
            return this;
        }
        
        public Builder userRating(Double userRating) {
            this.userRating = userRating;
            return this;
        }
        
        public Builder userRatingCount(int userRatingCount) {
            this.userRatingCount = userRatingCount;
            return this;
        }
        
        public Builder availableTime(String availableTime) {
            this.availableTime = availableTime;
            return this;
        }
        
        public ProfileResponseDto build() {
            return new ProfileResponseDto(memberId, profileImage, nickname, currentTown, 
                    userRating, userRatingCount, availableTime);
        }
    }
}
