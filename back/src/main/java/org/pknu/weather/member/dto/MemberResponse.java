package org.pknu.weather.member.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pknu.weather.member.enums.Sensitivity;


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

        private String levelKey;

        private String rankName;

        private Long exp;

        private Long nextLevelRequiredExp;

    }

    @Getter
    @Builder
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberLevelUpDTO {
        private Boolean isLevelUp;
        private String previousLevelRankName;
        private String currentLevelRankName;
    }

}
