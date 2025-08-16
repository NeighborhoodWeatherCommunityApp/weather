package org.pknu.weather.alarm.service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.alarm.dto.AlarmRequestDTO;
import org.pknu.weather.alarm.entity.Alarm;
import org.pknu.weather.alarm.enums.SummaryAlarmTime;
import org.pknu.weather.alarm.repository.AlarmRepository;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlarmService 유닛 테스트")
class AlarmServiceTest {

    @InjectMocks
    private AlarmService alarmService;

    @Mock
    private AlarmRepository alarmRepository;

    @Mock
    private MemberRepository memberRepository;

    private MockedStatic<Alarm> mockedAlarmStatic;

    @BeforeEach
    void setUp() {
        mockedAlarmStatic = Mockito.mockStatic(Alarm.class);
    }

    @AfterEach
    void tearDown() {
        mockedAlarmStatic.close();
    }

    @Test
    void 알람_저장이_성공한다() {

        mockedAlarmStatic.when(Alarm::builder).thenCallRealMethod();


        Member member = Member.builder()
                .email("test_email")
                .build();

        // Given
        AlarmRequestDTO requestDTO = getAlarmRequestDTO();
        Alarm expectedCreatedAlarm = getAlarm(member);

        // Stubbing
        when(memberRepository.safeFindByEmail(eq("test_email"))).thenReturn(member);
        mockedAlarmStatic.when(() -> Alarm.createDefaultAlarm(eq(member), eq(requestDTO)))
                .thenReturn(expectedCreatedAlarm);
        when(alarmRepository.saveAndFlush(any(Alarm.class))).thenReturn(expectedCreatedAlarm);

        // When
        alarmService.saveAlarm("test_email", requestDTO);

        // Then
        verify(memberRepository).safeFindByEmail(eq("test_email"));
        mockedAlarmStatic.verify(() -> Alarm.createDefaultAlarm(eq(member), eq(requestDTO)));
        verify(alarmRepository).saveAndFlush(eq(expectedCreatedAlarm));
        verify(alarmRepository, times(1)).saveAndFlush(any(Alarm.class));
    }

    private static Alarm getAlarm(Member member) {
        return Alarm.builder()
                .id(1L)
                .member(member)
                .fcmToken("valid_fcm_token")
                .agreeTempAlarm(true)
                .agreePrecipAlarm(false)
                .agreeDustAlarm(true)
                .agreeUvAlarm(true)
                .agreeLiveRainAlarm(false)
                .summaryAlarmTimes(new HashSet<>(Set.of(SummaryAlarmTime.MORNING, SummaryAlarmTime.EVENING)))
                .build();
    }

    private static AlarmRequestDTO getAlarmRequestDTO() {
        return AlarmRequestDTO.builder()
                .fcmToken("valid_fcm_token")
                .agreeTempAlarm(true)
                .agreePrecipAlarm(false)
                .agreeDustAlarm(true)
                .agreeUvAlarm(true)
                .agreeLiveRainAlarm(false)
                .summaryAlarmTimes(new HashSet<>(Set.of(SummaryAlarmTime.MORNING, SummaryAlarmTime.EVENING)))
                .build();
    }


    @Test
    void 알람_수정이_성공한다() {

        Member member = Member.builder()
                .email("test_email")
                .build();

        mockedAlarmStatic.when(Alarm::builder).thenCallRealMethod();

        // Given
        AlarmRequestDTO requestDTO = getAlarmRequestDTO();

        Alarm foundAlarm = Alarm.builder()
                .id(1L)
                .fcmToken("valid_fcm_token")
                .agreeTempAlarm(false)
                .agreePrecipAlarm(true)
                .agreeDustAlarm(false)
                .agreeUvAlarm(false)
                .agreeLiveRainAlarm(true)
                .summaryAlarmTimes(new HashSet<>(Set.of(SummaryAlarmTime.AFTERNOON)))
                .build();

        Alarm expectedModifiedAlarm = getAlarm(member);
        when(memberRepository.safeFindByEmail(member.getEmail())).thenReturn(member);
        when(alarmRepository.findByFcmTokenAndMember(requestDTO.getFcmToken(), member)).thenReturn(Optional.of(foundAlarm));
        mockedAlarmStatic.when(() -> Alarm.modifyAlarm(eq(foundAlarm), eq(requestDTO))).thenReturn(expectedModifiedAlarm);
        when(alarmRepository.saveAndFlush(any(Alarm.class))).thenReturn(expectedModifiedAlarm);

        alarmService.modifyAlarm(member.getEmail(), requestDTO);


        // Then
        verify(alarmRepository).findByFcmTokenAndMember(requestDTO.getFcmToken(), member);
        mockedAlarmStatic.verify(() -> Alarm.modifyAlarm(eq(foundAlarm), eq(requestDTO)));
        verify(alarmRepository).saveAndFlush(eq(expectedModifiedAlarm));
        verify(alarmRepository, times(1)).saveAndFlush(any(Alarm.class));
    }

    @Test
    void  fcmToken_을_찾을_수_없을_때_GeneralException이_발생한다() {
        Member member = Member.builder()
                .email("test_email")
                .build();


        // Given
        AlarmRequestDTO requestDTO = AlarmRequestDTO.builder()
                .fcmToken("non_existent_fcm_token")
                .build();

        when(memberRepository.safeFindByEmail(member.getEmail())).thenReturn(member);
        when(alarmRepository.findByFcmTokenAndMember(requestDTO.getFcmToken(), member)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> {
            alarmService.modifyAlarm(member.getEmail(),requestDTO);
        })
                .isInstanceOf(GeneralException.class)
                .extracting("code")
                .isEqualTo(ErrorStatus._FCMTOKEN_NOT_FOUND);


        verify(alarmRepository).findByFcmTokenAndMember(requestDTO.getFcmToken(), member);
        mockedAlarmStatic.verifyNoInteractions();
        verify(alarmRepository, never()).saveAndFlush(any(Alarm.class));
    }
}