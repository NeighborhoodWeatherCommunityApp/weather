package org.pknu.weather.member.exp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.member.exp.CreatePostExpRewardLimitPolicy;
import org.pknu.weather.post.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
class CreatePostExpRewardLimitPolicyTest {
    @Mock
    PostRepository postRepository;

    CreatePostExpRewardLimitPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new CreatePostExpRewardLimitPolicy(postRepository);
    }

    @Test
    void 게시글_작성_5회_미만이면_보상된다() {
        when(postRepository.countTodayPostByMemberId(1L)).thenReturn(3);

        boolean result = policy.canReward(1L);

        assertThat(result).isTrue();
    }

    @Test
    void 게시글_작성_5회_이상이면_보상되지_않는다() {
        when(postRepository.countTodayPostByMemberId(1L)).thenReturn(4);

        boolean result = policy.canReward(1L);

        assertThat(result).isFalse();
    }
}