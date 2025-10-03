package org.pknu.weather.security.filter;

import static org.pknu.weather.security.PublicPaths.PERMIT_ALL_PATHS;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.member.repository.MemberRepository;
import org.pknu.weather.security.exception.TokenException;
import org.pknu.weather.security.jwt.TokenValidator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private final TokenValidator tokenValidator;
    private final MemberRepository memberRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (shouldSkip(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Token Check Filter............................");

        try {
            authenticateRequest(request);
        } catch (TokenException tokenException){
            tokenException.sendResponseError(response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void authenticateRequest(HttpServletRequest request) throws TokenException {
        String token = extractAccessToken(request);
        Map<String, Object> claims = tokenValidator.validateAccessToken(token);

        String email = (String) claims.get("email");

        Member member = memberRepository.findMemberWithRolesByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        List<GrantedAuthority> authorities = member.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());

        Authentication auth =
                new UsernamePasswordAuthenticationToken(member, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private String extractAccessToken(HttpServletRequest request) throws TokenException {
        String header = request.getHeader(AUTH_HEADER);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            log.error("인증 헤더가 없거나 잘못된 형식으로 지정되었습니다: {}", header);
            throw new TokenException(ErrorStatus.ACCESS_TOKEN_NOT_ACCEPTED);
        }

        return header.substring(TOKEN_PREFIX.length());
    }

    private boolean shouldSkip(String uri) {
        return PERMIT_ALL_PATHS.contains(uri);
    }

}
