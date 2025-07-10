package org.pknu.weather.repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.config.DataJpaTestConfig;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Recommendation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(DataJpaTestConfig.class)
class RecommendationRepositoryTest {

    @Autowired
    RecommendationRepository recommendationRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    EntityManager em;

    @Test
    void 좋아요_취소후_다시눌렀을때_true반환() {
        // given
        Member sender = memberRepository.save(TestDataCreator.getBusanMember("sender"));
        Member receiver = memberRepository.save(TestDataCreator.getBusanMember("receiver"));
        Post post = postRepository.save(TestDataCreator.getPost(receiver));

        Recommendation recommendation = Recommendation.builder()
                .member(sender)
                .post(post)
                .build();

        recommendationRepository.save(recommendation);

        // when
        recommendationRepository.softDeleteByMemberAndPostId(sender, post.getId());
        Boolean result = recommendationRepository.isRecommend(sender.getId(), post.getId());

        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void 좋아요_취소하면_소프트삭제() {
        // given
        Member sender = memberRepository.save(TestDataCreator.getBusanMember("sender"));
        Member receiver = memberRepository.save(TestDataCreator.getBusanMember("receiver"));
        Post post = postRepository.save(TestDataCreator.getPost(receiver));

        Recommendation recommendation = recommendationRepository.save(
                Recommendation.builder()
                        .member(sender)
                        .post(post)
                        .build()
        );

        // when
        recommendationRepository.softDeleteByMemberAndPostId(sender, post.getId());
        em.flush();
        em.clear();

        // then
        Recommendation result = recommendationRepository.findById(recommendation.getId()).get();
        Assertions.assertThat(result.getDeleted()).isTrue();
        Assertions.assertThat(result.getMember().getId()).isEqualTo(sender.getId());
        Assertions.assertThat(result.getPost().getId()).isEqualTo(post.getId());
    }
}