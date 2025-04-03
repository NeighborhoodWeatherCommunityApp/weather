package org.pknu.weather.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.common.TestUtil;
import org.pknu.weather.common.utils.LocalUploaderUtils;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.common.Level;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.security.util.JWTUtil;
import org.pknu.weather.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerV2Test {
    @SpyBean
    MemberService memberService;

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    LocalUploaderUtils localUploaderUtils;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JWTUtil jwtUtil;

    @Test
    void 사용자정보조회성공() throws Exception {
        // given
        Member member = Member.builder()
                .id(1L)
                .location(TestDataCreator.getBusanLocation())
                .email("test@naver.com")
                .build();
        String jwt = TestUtil.generateJwtToken(jwtUtil, member);
        when(memberRepository.findMemberByEmail(any(String.class))).thenReturn(Optional.of(member));

        // when
        ResultActions result = mockMvc.perform(get("/api/v2/member/info")
                .header("Authorization", jwt));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.email").value(member.getEmail()))
                .andExpect(jsonPath("$.result.nickname").value(member.getNickname()))
                .andExpect(jsonPath("$.result.levelName").value(member.getLevel().name()))
                .andExpect(jsonPath("$.result.levelTitle").value(member.getLevel().getTitle()))
                .andExpect(jsonPath("$.result.exp").value(member.getExp()))
                .andExpect(jsonPath("$.result.nextLevelRequiredExp").value(
                        Level.getNextLevel(member.getLevel()).getRequiredExp()));

        verify(memberService).findFullMemberInfoByEmail(any(String.class));
    }
}
