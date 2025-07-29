package org.pknu.weather.member.auth.generator;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.Role;
import org.pknu.weather.member.auth.userInfo.SocialUserInfo;
import org.pknu.weather.member.auth.userInfo.UserInfoProvider;
import org.pknu.weather.member.service.MemberService;
import org.pknu.weather.security.jwt.JWTUtil;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppTokenGenerator {
    private final MemberService memberService;
    private final JWTUtil jwtUtil;

    public SocialUserInfo getUserInfo(UserInfoProvider UserInfoProvider, String token) {
        return UserInfoProvider.getUserInfo(token);
    }

    public Map<String, String> generateAppToken(Map<String, Object> userInfo) {

        log.debug("generateAppToken start----------------------------------------");
        String email = (String)userInfo.get("email");

        String isNewMember = saveMember(email);

        log.debug("Generate AppToken ...................");
        String accessToken = jwtUtil.generateToken(userInfo,3);
        String refreshToken = jwtUtil.generateToken(userInfo,30);

        return Map.of("accessToken", accessToken,
                "refreshToken", refreshToken,
                "isNewMember", isNewMember);

    }

    private String saveMember(String email) {
        Optional<Member> appMember = memberService.findMemberByEmail(email);
        appMember.orElseGet(() -> memberService.saveMember(Member.builder()
                        .email(email)
                        .roles(Set.of(Role.ROLE_MEMBER))
                .build()));

        return String.valueOf(appMember.isEmpty());
    }
}
