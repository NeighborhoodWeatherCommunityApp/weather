package org.pknu.weather.service;


import static org.pknu.weather.dto.converter.AlarmConverter.toAlarmResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.domain.Alarm;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.dto.AlarmRequestDTO;
import org.pknu.weather.dto.AlarmResponseDTO;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.repository.AlarmRepository;
import org.pknu.weather.member.repository.MemberRepository;
import org.pknu.weather.service.handler.AlarmHandlerFactory;
import org.pknu.weather.domain.common.AlarmType;
import org.pknu.weather.service.handler.ArgsAlarmHandler;
import org.pknu.weather.service.handler.NoArgsAlarmHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AlarmService {

    private final AlarmHandlerFactory handlerFactory;
    private final AlarmRepository alarmRepository;
    private final MemberRepository memberRepository;

    public void trigger(AlarmType alarmType) {
        NoArgsAlarmHandler handler = handlerFactory.getNoArgsAlarmHandler(alarmType);
        handler.handleRequest();
    }

    public <T> void trigger(AlarmType alarmType, T args) {
        ArgsAlarmHandler<T> handler = handlerFactory.getArgsAlarmHandler(alarmType, args);
        handler.handleRequest(args);
    }

    public void saveAlarm(String email, AlarmRequestDTO alarmRequestDTO) {
        Member member = memberRepository.safeFindByEmail(email);
        Alarm createdAlarm = Alarm.createDefaultAlarm(member, alarmRequestDTO);
        alarmRepository.saveAndFlush(createdAlarm);
    }

    public void modifyAlarm(String email, AlarmRequestDTO alarmRequestDTO) {
        Member member = memberRepository.safeFindByEmail(email);
        Alarm foundAlarm = alarmRepository.findByFcmTokenAndMember(alarmRequestDTO.getFcmToken(),member)
                .orElseThrow(() -> new GeneralException(ErrorStatus._FCMTOKEN_NOT_FOUND));

        Alarm modifiedAlarm = Alarm.modifyAlarm(foundAlarm, alarmRequestDTO);
        alarmRepository.saveAndFlush(modifiedAlarm);
    }

    public AlarmResponseDTO getAlarm(String email, String fcmToken) {
        Member member = memberRepository.safeFindByEmail(email);
        Alarm foundAlarm = alarmRepository.findByFcmTokenAndMember(fcmToken,member)
                .orElseThrow(() -> new GeneralException(ErrorStatus._FCMTOKEN_NOT_FOUND));

        return toAlarmResponseDto(foundAlarm);
    }

}
