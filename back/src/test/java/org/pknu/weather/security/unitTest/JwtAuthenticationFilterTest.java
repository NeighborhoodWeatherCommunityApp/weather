package org.pknu.weather.security.unitTest;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultHeader;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.member.repository.MemberRepository;
import org.pknu.weather.security.filter.JwtAuthenticationFilter;
import org.pknu.weather.security.util.JWTUtil;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JWTUtil jwtUtil;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtUtil, memberRepository);
    }

    @Test
    void 인증이_필요없는_공개된_URL은_토큰없이_통과된다() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        doAnswer(invocation -> {
            HttpServletResponse res = invocation.getArgument(1);
            res.setStatus(200);
            res.getWriter().write("PASSED");
            return null;
        }).when(filterChain).doFilter(any(), any());

        // when
        filter.doFilter(request, response, filterChain);

        // then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).isEqualTo("PASSED");
    }

    @Test
    void 잘못된_형식의_토큰_입력시_malformed예외가_발생한다() throws IOException, ServletException {
        String malformedToken = "malformedToken";
        MockHttpServletRequest request = setupRequestWithToken(malformedToken);
        MockHttpServletResponse response = new MockHttpServletResponse();

        given(jwtUtil.validateToken(malformedToken))
                .willThrow(new MalformedJwtException("Malformed"));


        // when
        filter.doFilter(request, response, filterChain);

        // then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("JWT_401_3");

    }

    @Test
    void 만기된_토큰_입력시_expired예외가_발생한다() throws ServletException, IOException {
        String expiredToken = "expiredToken";
        MockHttpServletRequest request = setupRequestWithToken(expiredToken);
        MockHttpServletResponse response = new MockHttpServletResponse();

        given(jwtUtil.validateToken(expiredToken))
                .willThrow(new ExpiredJwtException(
                        new DefaultHeader<>(), new DefaultClaims(), "expired"
                ));

        // when
        filter.doFilter(request, response, filterChain);

        // then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("JWT_401_5");

    }

    @Test
    void 서명이_잘못된_토큰의_경우에는_Signatured예외가_발생한다() throws ServletException, IOException {
        String invalidToken = "badSignaturedToken";
        MockHttpServletRequest request = setupRequestWithToken(invalidToken);
        MockHttpServletResponse response = new MockHttpServletResponse();

        given(jwtUtil.validateToken(invalidToken))
                .willThrow(new SignatureException("Signatured"));

        // when
        filter.doFilter(request, response, filterChain);

        // then
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("JWT_401_4");

    }

    private MockHttpServletRequest setupRequestWithToken(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test");
        request.addHeader("Authorization", "Bearer " + token);
        return request;
    }
}
