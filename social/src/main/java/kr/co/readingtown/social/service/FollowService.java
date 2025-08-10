package kr.co.readingtown.social.service;

import kr.co.readingtown.social.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;

    public boolean isFollowing(Long fromMemberId, Long toMemberId) {
        if (fromMemberId == null || toMemberId == null) return false;
        if (fromMemberId.equals(toMemberId)) {
            //Todo 자기자신 팔로우 방지 exception
            return false;
        }
        return followRepository.existsByFromFollowerIdAndToFollowingId(fromMemberId, toMemberId);
    }

    public Map<Long, Boolean> isFollowingMany(Long fromMemberId, List<Long> targetMemberIds) {
        if (targetMemberIds == null || targetMemberIds.isEmpty()) return Collections.emptyMap();

        // DB 한 번으로 내가 팔로우 중인 대상들만 골라오기
        Set<Long> followed = followRepository.findFollowingIdsIn(fromMemberId, targetMemberIds);

        // 요청 순서 그대로 Boolean 매핑
        return targetMemberIds.stream()
                .collect(Collectors.toMap(id -> id, followed::contains, (a, b) -> a, LinkedHashMap::new));
    }
}
