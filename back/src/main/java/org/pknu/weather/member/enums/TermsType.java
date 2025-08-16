package org.pknu.weather.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TermsType {
    // 필수 약관
    SERVICE_TERMS("서비스 이용약관", 100),
    PRIVACY_POLICY("개인정보처리동의서", 101),
    LOCATION_SERVICE_TERMS("위치기반서비스 이용약관", 102),

    // 선택 역관
    PUSH_NOTIFICATION("앱 푸쉬 알림 수신 동의", 203);
    ;

    private final String termsName;
    private final int termsCode;
}
