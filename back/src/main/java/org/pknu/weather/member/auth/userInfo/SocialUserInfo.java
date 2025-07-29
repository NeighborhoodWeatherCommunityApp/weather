package org.pknu.weather.member.auth.userInfo;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;

@Builder
public class SocialUserInfo {
    private String type;
    private String email;
    public SocialUserInfo(String type, String email) {
        validateUserInfo(type, email);
        this.type = type;
        this.email = email;
    }

    private void validateUserInfo(String type, String email) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Type cannot be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
    }

    public Map<String, Object> getUserInfo(){
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("type", type);
        userInfo.put("email", email);
        return userInfo;
    }

}
