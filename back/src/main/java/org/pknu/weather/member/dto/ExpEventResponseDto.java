package org.pknu.weather.member.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpEventResponseDto {
    final String expEvent;
    final String rewardName;
    final Long rewardExpAmount;
    final Boolean allowApiRequest;
}
