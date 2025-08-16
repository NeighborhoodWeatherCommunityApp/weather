package org.pknu.weather.member.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpRewardResponseDTO {
    private String nickname;
    private Integer level;
    private Long exp;
    private Long rewardExpAmount;
}
