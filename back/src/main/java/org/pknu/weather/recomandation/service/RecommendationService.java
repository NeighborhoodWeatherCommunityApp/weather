package org.pknu.weather.recomandation.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.post.entity.Post;
import org.pknu.weather.recomandation.entity.Recommendation;
import org.pknu.weather.recomandation.converter.RecommendationConverter;
import org.pknu.weather.member.event.RecommendEvent;
import org.pknu.weather.member.event.RecommendedEvent;
import org.pknu.weather.member.repository.MemberRepository;
import org.pknu.weather.post.repository.PostRepository;
import org.pknu.weather.recomandation.repository.RecommendationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final RecommendationRepository recommendationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public boolean addRecommendation(String senderEmail, Long postId) {
        Member sender = memberRepository.safeFindByEmail(senderEmail);
        Post post = postRepository.safeFindById(postId);

        if (post.getMember().equals(sender)) {
            // 자기 자신의 글인 경우: 추천은 되지만 경험치는 지급되지 않음
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
        Optional<Recommendation> optionalRecommendation = recommendationRepository
                .findByMemberIdAndPostId(sender.getId(), post.getId());

        if (optionalRecommendation.isEmpty()) {
            createRecommendation(sender, post);
            return;
        }

        Recommendation recommendation = optionalRecommendation.get();
        if (recommendation.isDeleted()) {
            recommendation.undoSoftDelete();
        } else {
            recommendation.softDelete();
        }
    }

    private void rewardRecommendation(Member sender, Post post) {
        eventPublisher.publishEvent(new RecommendEvent(sender.getEmail()));
        eventPublisher.publishEvent(new RecommendedEvent(post.getMember().getEmail()));
    }
}
