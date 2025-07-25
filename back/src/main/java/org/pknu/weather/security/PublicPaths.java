package org.pknu.weather.security;

/**
 * Spring Security 인증 및 인가 설정에서 공통으로 사용되는 "인증 생략 경로" 목록입니다.
 *
 * 이 경로들은 다음의 두 가지 용도로 사용됩니다:
 * - JwtAuthenticationFilter에서 인증을 수행하지 않고 요청을 필터 체인에 넘기기 위한 예외 경로
 * - SecurityConfig의 .requestMatchers(...).permitAll() 설정에 사용되는 인가 예외 경로
 *
 * 인증과 인가가 모두 필요 없는 공개 API 경로로,
 * 인증 필터와 인가 설정 간의 동기화를 위해 한 곳에서 정의하여 관리합니다.
 */
public class PublicPaths {
    public static final String[] PERMIT_ALL_PATHS = {
            "/token",
            "/refreshToken",
            "/health-check"
    };
}
