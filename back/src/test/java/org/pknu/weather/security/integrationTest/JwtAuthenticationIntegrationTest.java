package org.pknu.weather.security.integrationTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pknu.weather.config.EmbeddedRedisConfig;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.member.auth.userInfo.SocialUserInfo;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.enums.Role;
import org.pknu.weather.member.repository.MemberRepository;
import org.pknu.weather.security.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Autowired
    CacheManager cm;

    private static final String ACTUATOR_TEST_PATH = "/actuator/prometheus";
    private static final String API_TEST_PATH = "/api/v1/member/info";

    @BeforeEach
    void init() {
        cm.getCacheNames()
                .forEach(name -> Objects.requireNonNull(cm.getCache(name)).clear());
    }

    @Test
    void 유효한_토큰이_없는_사용자는_API에_접근할_수_없다() throws Exception {
        mockMvc.perform(get(API_TEST_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 유효한_토큰으로_접근시_API_응답이_정상적으로_이루어진다() throws Exception {
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
        mockMvc.perform(get(API_TEST_PATH)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"isSuccess\":true")));    }

    @Test
    void 잘못된_토큰은_401로_거부된다() throws Exception {
        String malformedToken = "malformedToken";

        mockMvc.perform(get(API_TEST_PATH)
                        .header("Authorization", "Bearer " + malformedToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("\"JWT_401_3\"")));
    }

    @Test
    void 일반사용자는_액추에이터_관련_api_접근시_제한된다() throws Exception {
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

        mockMvc.perform(get(ACTUATOR_TEST_PATH)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void 관리자권한을_가진_유저는_액추에이터_관련_api_접근시_통과() throws Exception {
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

        mockMvc.perform(get(ACTUATOR_TEST_PATH)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

    }
}