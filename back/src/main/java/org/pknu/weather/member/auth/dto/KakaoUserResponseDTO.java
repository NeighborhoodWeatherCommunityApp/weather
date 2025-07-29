package org.pknu.weather.member.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class KakaoUserResponseDTO {

    private Long id;
    private LocalDateTime connected_at;
    private KakaoAccount kakao_account;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class KakaoAccount{
        private String email;
    }
}
