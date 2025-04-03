package org.pknu.weather.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.exp.Level;
import org.pknu.weather.dto.MemberResponse.MemberResponseWithAddressDTO;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Spy
    @InjectMocks
    MemberService memberService;

    @Test
    void 사용자정보조회성공_레벨과경험치함께반환() {
        // given
        Member member = TestDataCreator.getBusanMember();
        when(memberRepository.findMemberByEmail(any(String.class))).thenReturn(Optional.of(member));

        // when
        MemberResponseWithAddressDTO result = memberService.findFullMemberInfoByEmail(member.getEmail());

        // then
        assertThat(result.getLocationId()).isEqualTo(member.getLocation().getId());
        assertThat(result.getNickname()).isEqualTo(member.getNickname());
        assertThat(result.getEmail()).isEqualTo(member.getEmail());
        assertThat(result.getSensitivity()).isEqualTo(member.getSensitivity());
        assertThat(result.getLevelName()).isEqualTo(member.getLevel().name());
        assertThat(result.getLevelTitle()).isEqualTo(member.getLevel().getTitle());
        assertThat(result.getExp()).isEqualTo(member.getExp());
        assertThat(result.getNextLevelRequiredExp()).isEqualTo(Level.getNextLevel(member.getLevel()).getRequiredExp());
    }

    @Test
    void 사용자정보조회실패_예외발생() {
        Member member = TestDataCreator.getBusanMember();
        when(memberRepository.findMemberByEmail(any(String.class))).thenReturn(Optional.empty());

        assertThrows(GeneralException.class, () -> {
            memberService.findFullMemberInfoByEmail(member.getEmail());
        });
    }
}