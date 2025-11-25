package kr.co.readingtown.social.service;

import kr.co.readingtown.social.domain.Follow;
import kr.co.readingtown.social.exception.FollowException;
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
        if (fromMemberId.equals(toMemberId)) { //자기자신 팔로우 방지
            throw new FollowException.selfFollowNotAllowed();
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

    @Transactional
    public void follow(Long fromMemberId, Long toMemberId) {
        if (Objects.equals(fromMemberId, toMemberId)) {
            throw new FollowException.selfFollowNotAllowed();
        }
        //이미 팔로우가 존재하면 무시
        boolean exists = followRepository.existsByFromFollowerIdAndToFollowingId(fromMemberId, toMemberId);
        if (exists) return;

        Follow follow = Follow.builder()
                .fromFollowerId(fromMemberId)
                .toFollowingId(toMemberId)
                .build();

        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(Long fromMemberId, Long toMemberId) {
        //팔로우가 안되어있으면 무시
        followRepository.deleteByFromFollowerIdAndToFollowingId(fromMemberId, toMemberId);
    }

    public List<Long> getFollowingIds(Long memberId) {
        return followRepository.findFollowingIds(memberId);
    }

    public List<Long> getFollowerIds(Long memberId) {
        return followRepository.findFollowerIds(memberId);
    }

    @Transactional
    public void deleteFollowRelation(Long memberId) {

        followRepository.deleteAllByMemberIdInFollowRelation(memberId);
    }
}