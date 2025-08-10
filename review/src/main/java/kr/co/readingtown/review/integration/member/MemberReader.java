package kr.co.readingtown.review.integration.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MemberReader {

    private final MemberClient memberClient;

    public Map<Long, String> getMemberNames(List<Long> memberIds) {

        if (memberIds == null || memberIds.isEmpty())
            return Collections.emptyMap();
        List<Long> distinctIds = memberIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        return memberClient.getMembersName(distinctIds);
    }
}
