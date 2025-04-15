package org.pknu.weather.dto.converter;

import org.pknu.weather.domain.exp.ExpEvent;
import org.pknu.weather.dto.ExpEventResponseDto;

public class ExpEventResponseConverter {

    public static ExpEventResponseDto toExpResponseDto(ExpEvent expEvent) {
        return ExpEventResponseDto.builder()
                .expEvent(expEvent.name())
                .rewardName(expEvent.getRewardName())
                .rewardExpAmount(expEvent.getRewardExpAmount())
                .allowApiRequest(true)
                .build();
    }
}
