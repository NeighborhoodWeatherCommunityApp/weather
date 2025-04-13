package org.pknu.weather.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.common.PostType;
import org.pknu.weather.domain.exp.ExpEvent;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.pknu.weather.repository.RecommendationRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceUnitTest {

    @Mock
    PostRepository postRepository;

    @Mock
    RecommendationRepository recommendationRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    ExpRewardService expRewardService;

    @InjectMocks
    PostService postService;

    @Test
    void 추천자가_좋아요를_눌렀을때_추천자와_피추천자는_각각_경험치를_획득합니다() {
        // given
        Member sender = TestDataCreator.getBusanMember("sender");   // 좋아요 주는 사람
        Member receiver = TestDataCreator.getBusanMember("receiver"); // 받는 사람
        Post post = Post.builder()
                .id(1L)
                .location(receiver.getLocation())
                .member(receiver)
                .content("content")
                .postType(PostType.WEATHER)
                .build();

        when(memberRepository.safeFindByEmail(sender.getEmail())).thenAnswer(invocation -> {
            Object email = invocation.getArgument(0);

            if(email.equals(sender.getEmail())) {
                return sender;
            } else if (email.equals(receiver.getEmail())) {
                return receiver;
            } else {
                throw new IllegalArgumentException("Unknown email: " + email);
            }
        });

        when(postRepository.safeFindById(post.getId())).thenReturn(post);
        when(recommendationRepository.isRecommend(sender.getId(), post.getId())).thenReturn(true);

        // rewardExp() 호출 시 직접 경험치 증가시키는 로직을 스텁
        doAnswer(invocation -> {
            String email = invocation.getArgument(0);
            ExpEvent event = invocation.getArgument(1);

            if (email.equals(sender.getEmail())) {
                sender.addExp(event.getRewardExpAmount());
            } else if (email.equals(receiver.getEmail())) {
                receiver.addExp(event.getRewardExpAmount());
            }
            return null;
        }).when(expRewardService).rewardExp(anyString(), any(ExpEvent.class));

        // when
        postService.addRecommendation(sender.getEmail(), post.getId());

        // then
        Assertions.assertThat(sender.getExp()).isEqualTo(ExpEvent.RECOMMEND.getRewardExpAmount());
        Assertions.assertThat(receiver.getExp()).isEqualTo(ExpEvent.RECOMMENDED.getRewardExpAmount());
    }

    @Test
    void 좋아요를_클릭한_유저가_좋아요를_취소하고_다시_좋아요를_눌러도_좋아요를_받는쪽과_클릭한쪽_모두_경험치를_획득하지_못합니다() {

    }
}
