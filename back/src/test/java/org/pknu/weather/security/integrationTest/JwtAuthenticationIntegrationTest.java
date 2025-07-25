package org.pknu.weather.security.integrationTest;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.pknu.weather.config.EmbeddedRedisConfig;
import org.pknu.weather.domain.Location;
import org.pknu.weather.member.Role;
import org.pknu.weather.member.auth.userInfo.SocialUserInfo;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.repository.MemberRepository;
import org.pknu.weather.security.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@Import(EmbeddedRedisConfig.class) //액추에이터의 헬스 정보에 접근할 때 레디스의 상태가 down일 경우, 503에러 발생
class JwtAuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 인증되지_않은_사용자는_보호된_API에_401을_응답받는다() throws Exception {
        mockMvc.perform(get("/api/v1/member/info"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 유효한_토큰으로_접근시_보호된_API에_200_응답() throws Exception {
        // given
        String email = "test@example.com";
        Member member = Member.builder()
                .email(email)
                .roles(Set.of(Role.ROLE_MEMBER))
                .location(Location.builder().build())
                .build();
        memberRepository.save(member);

        SocialUserInfo userInfo = new SocialUserInfo("kakao", email);
        String token = jwtUtil.generateToken(userInfo.getUserInfo(), 3);

        // when & then
        mockMvc.perform(get("/api/v1/member/info")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"isSuccess\":true")));    }

    @Test
    void 잘못된_토큰은_401로_거부된다() throws Exception {
        String malformedToken = "malformedToken";

        mockMvc.perform(get("/api/v1/member/info")
                        .header("Authorization", "Bearer " + malformedToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("\"JWT_401_3\"")));
    }

    @Test
    void 일반사용자는_액추에이터_접근시_403반환() throws Exception {
        // given
        String email = "test@example.com";
        Member member = Member.builder()
                .email(email)
                .roles(Set.of(Role.ROLE_MEMBER))
                .location(Location.builder().build())
                .build();
        memberRepository.save(member);

        SocialUserInfo userInfo = new SocialUserInfo("kakao", email);
        String token = jwtUtil.generateToken(userInfo.getUserInfo(), 3);

        mockMvc.perform(get("/actuator/health")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void 관리자는_액추에이터_접근시_통과() throws Exception {
        // given
        String email = "test@example.com";
        Member member = Member.builder()
                .email(email)
                .roles(Set.of(Role.ROLE_MEMBER, Role.ROLE_ADMIN))
                .location(Location.builder().build())
                .build();
        memberRepository.save(member);

        SocialUserInfo userInfo = new SocialUserInfo("kakao", email);
        String token = jwtUtil.generateToken(userInfo.getUserInfo(), 3);

        mockMvc.perform(get("/actuator/health")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }
}