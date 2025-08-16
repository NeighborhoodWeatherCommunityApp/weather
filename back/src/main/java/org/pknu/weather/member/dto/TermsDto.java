package org.pknu.weather.member.dto;


import lombok.*;

@Data
@Builder
public class TermsDto {
    private Boolean isServiceTermsAgreed;
    private Boolean isPrivacyPolicyAgreed;
    private Boolean isLocationServiceTermsAgreed;
    private Boolean isPushNotificationAgreed;
}
