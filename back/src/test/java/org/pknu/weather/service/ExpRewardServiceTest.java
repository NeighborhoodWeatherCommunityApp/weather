package org.pknu.weather.service;

import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.common.ExpEvent;
import org.pknu.weather.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
public class ExpRewardServiceTest {
    @Mock
    MemberRepository memberRepository;

    @Spy
    @InjectMocks
    ExpRewardService expRewardService;

    @ParameterizedTest
    @EnumSource(ExpEvent.class)
    void 행위에_따라_경험치가_다르게_증가(ExpEvent expEvent) {
        // given
        Member member = TestDataCreator.getBusanMember();
        when(memberRepository.safeFindByEmail(member.getEmail())).thenReturn(member);

        // when
        expRewardService.rewardExp(member.getEmail(), expEvent);

        // then
        Member result = memberRepository.safeFindByEmail(member.getEmail());
        Assertions.assertThat(result.getNickname()).isEqualTo(member.getNickname());
        Assertions.assertThat(result.getLevel()).isEqualTo(member.getLevel());
        Assertions.assertThat(result.getExp()).isEqualTo(member.getExp());
    }
}
