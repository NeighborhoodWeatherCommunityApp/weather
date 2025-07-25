package org.pknu.weather.member.converter;

import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.domain.exp.Level;
import org.pknu.weather.member.dto.MemberResponse;
import org.pknu.weather.member.dto.MemberResponse.MemberLevelUpDTO;

@Slf4j
public class MemberResponseConverter {

    public static MemberResponse.MemberResponseDTO toMemberResponseDTO(Member member) {
        return MemberResponse.MemberResponseDTO.builder()
                .email(member.getEmail())
                .sensitivity(member.getSensitivity())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .build();
    }

    public static MemberResponse.MemberResponseWithAddressDTO toMemberResponseWithAddressDTO(Member member) {
        return MemberResponse.MemberResponseWithAddressDTO.builder()
                .province(member.getLocation().getProvince())
                .city(member.getLocation().getCity())
                .street(member.getLocation().getStreet())
                .locationId(member.getLocation().getId())
                .email(member.getEmail())
                .sensitivity(member.getSensitivity())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .levelKey(member.getLevel().name())
                .rankName(member.getLevel().getRankName())
                .exp(member.getExp())
                .nextLevelRequiredExp(Level.getNextLevel(member.getLevel()).getRequiredExp())
                .build();
    }

    public static MemberLevelUpDTO toMemberLevelDTO(Level prevLevel, Level currentLevel) {
        return MemberLevelUpDTO.builder()
                .isLevelUp(prevLevel != currentLevel)
                .currentLevelRankName(currentLevel.getRankName())
                .previousLevelRankName(prevLevel.getRankName())
                .build();
    }
}
