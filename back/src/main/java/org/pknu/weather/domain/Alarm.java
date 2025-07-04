package org.pknu.weather.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.pknu.weather.domain.common.SummaryAlarmTime;
import org.pknu.weather.dto.AlarmRequestDTO;

import java.util.HashSet;
import java.util.Set;

@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "alarm", uniqueConstraints = {
        @UniqueConstraint(name = "UK_alarm_member_fcmToken", columnNames = {"member_id", "fcm_token"})})
public class Alarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String fcmToken;

    @ColumnDefault("false")
    private Boolean agreeTempAlarm; // 온도 알람

    @ColumnDefault("false")
    private Boolean agreePrecipAlarm; // 강수 알람

    @ColumnDefault("false")
    private Boolean agreeDustAlarm; // 미세먼지 알람

    @ColumnDefault("false")
    private Boolean agreeUvAlarm; // 자외선 알람

    @ColumnDefault("false")
    private Boolean agreeLiveRainAlarm; // 실시간 비 알람

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "alarm_summary_time", joinColumns = @JoinColumn(name = "alarm_id"))
    @Column(name = "summary_time")
    private Set<SummaryAlarmTime> summaryAlarmTimes;


    /**
     * summaryAlarmTimes는 인자로 값이 전달되면 해당 값을 사용하고, null이 전달되면 기본값(false 또는 빈 Set)을 사용합니다.
     *
     * @param member           알람과 연관될 Member 객체 (필수)
     * @param alarmRequestDTO  알람 생성에 연관된 정보들을 담은 DTO(필수)
     * @return 생성된 Alarm 객체
     * @throws IllegalArgumentException member 또는 fcmToken이 null이거나 비어있는 경우
     */
    public static Alarm createDefaultAlarm(
            Member member,
            AlarmRequestDTO alarmRequestDTO
    ) {
        validateParam(member, alarmRequestDTO);

        return Alarm.builder()
                .member(member)
                .fcmToken(alarmRequestDTO.getFcmToken().trim())
                .agreeTempAlarm(alarmRequestDTO.getAgreeTempAlarm() != null ? alarmRequestDTO.getAgreeTempAlarm() : false)
                .agreePrecipAlarm(alarmRequestDTO.getAgreePrecipAlarm() != null ? alarmRequestDTO.getAgreePrecipAlarm() : false)
                .agreeDustAlarm(alarmRequestDTO.getAgreeDustAlarm() != null ? alarmRequestDTO.getAgreeDustAlarm() : false)
                .agreeUvAlarm(alarmRequestDTO.getAgreeUvAlarm() != null ? alarmRequestDTO.getAgreeUvAlarm() : false)
                .agreeLiveRainAlarm(alarmRequestDTO.getAgreeLiveRainAlarm() != null ? alarmRequestDTO.getAgreeLiveRainAlarm() : false)
                .summaryAlarmTimes(alarmRequestDTO.getSummaryAlarmTimes())
                .build();
    }

    public static Alarm modifyAlarm(
            Alarm alarm,
            AlarmRequestDTO alarmRequestDTO
    ) {
        validateParamForModify(alarm, alarmRequestDTO);

        if (alarmRequestDTO.getAgreeTempAlarm() != null) {
            alarm.agreeTempAlarm = alarmRequestDTO.getAgreeTempAlarm();
        }

        if (alarmRequestDTO.getAgreePrecipAlarm() != null) {
            alarm.agreePrecipAlarm = alarmRequestDTO.getAgreePrecipAlarm();
        }

        if (alarmRequestDTO.getAgreeDustAlarm() != null) {
            alarm.agreeDustAlarm = alarmRequestDTO.getAgreeDustAlarm();
        }

        if (alarmRequestDTO.getAgreeUvAlarm() != null) {
            alarm.agreeUvAlarm = alarmRequestDTO.getAgreeUvAlarm();
        }

        if (alarmRequestDTO.getAgreeLiveRainAlarm() != null) {
            alarm.agreeLiveRainAlarm = alarmRequestDTO.getAgreeLiveRainAlarm();
        }

        if (alarmRequestDTO.getSummaryAlarmTimes() != null) {
            alarm.summaryAlarmTimes = new HashSet<>(alarmRequestDTO.getSummaryAlarmTimes());
        }

        return alarm;
    }

    private static void validateParam(Member member, AlarmRequestDTO alarmRequestDTO) {
        if (member == null) {
            throw new IllegalArgumentException("Member는 알람 생성에 필수 요소입니다.");
        }

        if (alarmRequestDTO == null) {
            throw new IllegalArgumentException("AlarmRequestDTO는 알람 생성에 필수 요소입니다.");
        }

        if (alarmRequestDTO.getFcmToken() == null || alarmRequestDTO.getFcmToken().trim().isEmpty()) {
            throw new IllegalArgumentException("fcmToken은 알람 생성에 필수 요소입니다.");
        }
    }

    private static void validateParamForModify(Alarm alarm, AlarmRequestDTO alarmRequestDTO) {
        if (alarm == null) {
            throw new IllegalArgumentException("수정할 기존 Alarm 객체는 필수입니다.");
        }
        if (alarmRequestDTO == null) {
            throw new IllegalArgumentException("AlarmRequestDTO는 알람 수정에 필수 요소입니다.");
        }
    }
}
