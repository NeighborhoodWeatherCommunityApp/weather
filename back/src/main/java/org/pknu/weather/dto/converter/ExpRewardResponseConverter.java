package org.pknu.weather.dto.converter;

import org.pknu.weather.member.entity.Member;
import org.pknu.weather.domain.exp.ExpEvent;
import org.pknu.weather.dto.ExpRewardResponseDTO;

public class ExpRewardResponseConverter {

    public static ExpRewardResponseDTO toExpRewardResponseDTO(Member member, ExpEvent expEvent) {
        return ExpRewardResponseDTO.builder()
                .nickname(member.getNickname())
                .level(member.getLevel().getLevelNumber())
                .exp(member.getExp())
                .rewardExpAmount(expEvent.getRewardExpAmount())
                .build();
    }
}
