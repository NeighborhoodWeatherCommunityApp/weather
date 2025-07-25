package org.pknu.weather.member.auth.userInfo;

import java.util.Map;

public class KakaoUserInfo extends SocialUserInfo {
    private Long kakaoId;
    public KakaoUserInfo(String type, String email, Long kakaoId) {
        super(type,email);
        validateKakaoId(kakaoId);
        this.kakaoId = kakaoId;
    }

    private void validateKakaoId(Long kakaoId) {
        if (kakaoId == null) {
            throw new IllegalArgumentException("kakaoId cannot be null");
        }
    }

    public Map<String, Object> getUserInfo(){
        Map<String, Object> userInfo = super.getUserInfo();
        userInfo.put("kakaoId", String.valueOf(kakaoId));
        return userInfo;
    }

}
