package org.pknu.weather.dto.converter;

import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.common.ExpEvent;
import org.pknu.weather.dto.ExpRewardResponseDTO;

public class ExpRewardResponseConverter {

    public static ExpRewardResponseDTO toExpRewardResponseDTO(Member member, ExpEvent expEvent) {
        return ExpRewardResponseDTO.builder()
                .nickname(member.getNickname())
                .level(member.getLevel().getLevel())
                .exp(member.getExp())
                .rewardExpAmount(expEvent.getRewardExpAmount())
                .build();
    }
}
