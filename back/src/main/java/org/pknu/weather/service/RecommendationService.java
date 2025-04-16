package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Recommendation;
import org.pknu.weather.domain.exp.ExpEvent;
import org.pknu.weather.dto.converter.RecommendationConverter;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.pknu.weather.repository.RecommendationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final RecommendationRepository recommendationRepository;
    private final ExpRewardService expRewardService;

    @Transactional
    public boolean addRecommendation(String senderEmail, Long postId) {
        Member sender = memberRepository.safeFindByEmail(senderEmail);
        Post post = postRepository.safeFindById(postId);

        // 자기 자신의 글인 경우: 추천은 되지만 경험치는 지급되지 않음
        if (post.getMember().equals(sender)) {
            createRecommendationIfNotExists(sender, post);
            return true;
        }

        Recommendation recommendation = recommendationRepository.findByMemberIdAndPostId(sender.getId(), postId)
                .orElse(null);

        if (recommendation == null) {
            createRecommendation(sender, post);
            rewardRecommendation(sender, post);
        } else if (recommendation.isDeleted()) {
            recommendation.undoSoftDelete();
        } else {
            recommendation.softDelete();
        }

        return true;
    }

    private void createRecommendation(Member sender, Post post) {
        Recommendation recommendation = RecommendationConverter.toRecommendation(sender, post);
        recommendationRepository.save(recommendation);
    }

    private void createRecommendationIfNotExists(Member sender, Post post) {
        boolean alreadyExists = recommendationRepository
                .findByMemberIdAndPostId(sender.getId(), post.getId()).isPresent();
        if (!alreadyExists) {
            createRecommendation(sender, post);
        }
    }

    private void rewardRecommendation(Member sender, Post post) {
        expRewardService.rewardExp(sender.getEmail(), ExpEvent.RECOMMEND);
        expRewardService.rewardExp(post.getMember().getEmail(), ExpEvent.RECOMMENDED);
    }
}
