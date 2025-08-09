package kr.co.readingtown.review.integration.member;

import kr.co.readingtown.review.exception.ReviewException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberValidator {

    private final MemberClient memberClient;


    // memberId 존재하는지 확인
    public void validateMemberExists(Long memberId) {

        boolean exists = memberClient.existsMember(memberId);
        if (!exists) {
            throw new ReviewException.ReviewMemberNotFound();
        }
    }
}
