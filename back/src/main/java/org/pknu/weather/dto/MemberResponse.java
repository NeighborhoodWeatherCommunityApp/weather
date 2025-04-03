package org.pknu.weather.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pknu.weather.domain.common.Sensitivity;


public class MemberResponse {

    @Getter
    @Builder
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberResponseDTO {

        private String email;

        private Sensitivity sensitivity;

        private String nickname;

        private String profileImage;

    }

    @Getter
    @Builder
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberResponseWithAddressDTO {
        private String email;

        private Sensitivity sensitivity;

        private String nickname;

        private String profileImage;

        private Long locationId;

        private String province;

        private String city;

        private String street;

        private String levelName;

        private String levelTitle;

        private Long exp;

        private Long nextLevelRequiredExp;

    }

}
