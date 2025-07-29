package org.pknu.weather.member.auth.userInfo;

public interface UserInfoProvider {
    SocialUserInfo getUserInfo(String token);
}
