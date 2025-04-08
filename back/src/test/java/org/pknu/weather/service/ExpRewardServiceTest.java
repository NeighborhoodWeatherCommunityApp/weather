package org.pknu.weather.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.exp.CreatePostExpRewardLimitPolicy;
import org.pknu.weather.domain.exp.ExpEvent;
import org.pknu.weather.domain.exp.ExpRewardLimitPolicy;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
public class ExpRewardServiceTest {
    @Mock
    MemberRepository memberRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    CreatePostExpRewardLimitPolicy createPostExpRewardLimitPolicy;

    @Mock
    Map<ExpEvent, ExpRewardLimitPolicy> policyMap;

    @InjectMocks
    ExpRewardService expRewardService;

    @ParameterizedTest
    @EnumSource(ExpEvent.class)
    void 행위에_따라_경험치가_다르게_증가(ExpEvent expEvent) {
        // given
        Member member = TestDataCreator.getBusanMember();
        Long beforeExp = member.getExp();
        when(memberRepository.safeFindByEmail(member.getEmail())).thenReturn(member);

        // when
        expRewardService.rewardExp(member.getEmail(), expEvent);

        // then
        assertThat(member.getExp()).isEqualTo(beforeExp + expEvent.getRewardExpAmount());
    }

    @Test
    void 게시글_작성_경험치는_하루_5회까지만_지급된다() {
        // given
        Member member = TestDataCreator.getBusanMember();
        when(memberRepository.safeFindByEmail(member.getEmail())).thenReturn(member);
        when(policyMap.get(ExpEvent.CREATE_POST)).thenReturn(createPostExpRewardLimitPolicy);
        when(policyMap.get(ExpEvent.CREATE_POST).canReward(member.getId())).thenReturn(false);

        // when
        expRewardService.rewardExp(member.getEmail(), ExpEvent.CREATE_POST);

        // then
        assertThat(member.getExp()).isEqualTo(0);
    }

    @Test
    void 게시글_작성_5회_이전이면_경험치가_지급된다() {
        // given
        Member member = TestDataCreator.getBusanMember();
        when(memberRepository.safeFindByEmail(member.getEmail())).thenReturn(member);
        when(policyMap.get(ExpEvent.CREATE_POST)).thenReturn(createPostExpRewardLimitPolicy);
        when(policyMap.get(ExpEvent.CREATE_POST).canReward(member.getId())).thenReturn(true);

        // when
        expRewardService.rewardExp(member.getEmail(), ExpEvent.CREATE_POST);

        // then
        assertThat(member.getExp()).isEqualTo(ExpEvent.CREATE_POST.getRewardExpAmount());
    }
}
