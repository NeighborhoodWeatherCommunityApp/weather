package org.pknu.weather.member.converter;

import org.pknu.weather.member.exp.ExpEvent;
import org.pknu.weather.member.dto.ExpEventResponseDto;

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
