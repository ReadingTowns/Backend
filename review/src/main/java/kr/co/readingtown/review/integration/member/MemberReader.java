package kr.co.readingtown.review.integration.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MemberReader {

    private final MemberClient memberClient;

    public Map<Long, String> getMembersName(List<Long> memberIds) {

        return memberClient.getMembersName(memberIds);
    }
}
