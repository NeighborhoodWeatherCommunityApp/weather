package org.pknu.weather.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.common.TestUtil;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.exp.Level;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.security.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerV2Test {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JWTUtil jwtUtil;

    @Test
    void 사용자정보조회성공() throws Exception {
        // given
        Member member = memberRepository.save(TestDataCreator.getBusanMember());
        String jwt = TestUtil.generateJwtToken(jwtUtil, member);

        // when
        ResultActions result = mockMvc.perform(get("/api/v2/member/info")
                .header("Authorization", jwt));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.email").value(member.getEmail()))
                .andExpect(jsonPath("$.result.nickname").value(member.getNickname()))
                .andExpect(jsonPath("$.result.levelKey").value(member.getLevel().name()))
                .andExpect(jsonPath("$.result.rankName").value(member.getLevel().getRankName()))
                .andExpect(jsonPath("$.result.exp").value(member.getExp()))
                .andExpect(jsonPath("$.result.nextLevelRequiredExp").value(
                        Level.getNextLevel(member.getLevel()).getRequiredExp()));
    }
}
