package org.pknu.weather.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pknu.weather.config.DataJpaTestConfig;
import org.pknu.weather.domain.Alarm;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.domain.common.SummaryAlarmTime;
import org.pknu.weather.dto.AlarmRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@Import(DataJpaTestConfig.class)
@DataJpaTest
public class AlarmRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AlarmRepository alarmRepository;

    @Test
    public void 알람_저장_테스트() {
        Member member = saveMember();
        Set<SummaryAlarmTime> summaryAlarmTimes = Set.of(SummaryAlarmTime.MORNING, SummaryAlarmTime.EVENING);


        AlarmRequestDTO alarmRequestDTO = AlarmRequestDTO.builder()
                .fcmToken("fcmToken")
                .agreeUvAlarm(true)
                .agreePrecipAlarm(true)
                .agreeTempAlarm(true)
                .agreeDustAlarm(true)
                .agreeLiveRainAlarm(false)
                .summaryAlarmTimes(summaryAlarmTimes)
                .build();


        Alarm savedAlarm = alarmRepository.saveAndFlush(Alarm.createDefaultAlarm(member, alarmRequestDTO));
        entityManager.flush();
        entityManager.clear();

        Optional<Alarm> foundAlarm = alarmRepository.findById(savedAlarm.getId());
        Assertions.assertThat(foundAlarm).isPresent()
                .get()
                .satisfies(testAlarm -> {
                    Assertions.assertThat(testAlarm.getId()).isEqualTo(savedAlarm.getId());
                    Assertions.assertThat(testAlarm.getFcmToken()).isEqualTo("fcmToken");
                    Assertions.assertThat(testAlarm.getAgreeUvAlarm()).isTrue();
                    Assertions.assertThat(testAlarm.getAgreeDustAlarm()).isTrue();
                    Assertions.assertThat(testAlarm.getAgreeTempAlarm()).isTrue();
                    Assertions.assertThat(testAlarm.getAgreePrecipAlarm()).isTrue();
                    Assertions.assertThat(testAlarm.getAgreeLiveRainAlarm()).isFalse();
                });

        Assertions.assertThat(foundAlarm.get().getMember()).isNotNull()
                .satisfies(foundMember -> {
                    Assertions.assertThat(foundMember.getId()).isEqualTo(savedAlarm.getMember().getId());
                    Assertions.assertThat(foundMember.getEmail()).isEqualTo(savedAlarm.getMember().getEmail());
                });

        Assertions.assertThat(foundAlarm.get().getSummaryAlarmTimes()).isNotNull()
                .satisfies(foundSummaryAlarmTimes -> {
                    Assertions.assertThat(foundAlarm.get().getSummaryAlarmTimes()).hasSize(summaryAlarmTimes.size());
                    Assertions.assertThat(foundAlarm.get().getSummaryAlarmTimes()).containsExactlyInAnyOrderElementsOf(summaryAlarmTimes);
                });
    }

    @Test
    public void 생략가능한_정보_생략했을_때_기본_값으로_저장한다() {
        Member member = saveMember();
        AlarmRequestDTO alarmRequestDTO = AlarmRequestDTO.builder()
                .fcmToken("fcmToken")
                .build();

        Alarm savedAlarm =  alarmRepository.saveAndFlush(Alarm.createDefaultAlarm(member, alarmRequestDTO));
        entityManager.flush();
        entityManager.clear();

        Optional<Alarm> foundAlarm = alarmRepository.findById(savedAlarm.getId());
        Assertions.assertThat(foundAlarm).isPresent()
                .get()
                .satisfies(testAlarm -> {
                    Assertions.assertThat(testAlarm.getId()).isEqualTo(savedAlarm.getId());
                    Assertions.assertThat(testAlarm.getFcmToken()).isEqualTo("fcmToken");
                    Assertions.assertThat(testAlarm.getAgreeUvAlarm()).isFalse();
                    Assertions.assertThat(testAlarm.getAgreeDustAlarm()).isFalse();
                    Assertions.assertThat(testAlarm.getAgreeTempAlarm()).isFalse();
                    Assertions.assertThat(testAlarm.getAgreePrecipAlarm()).isFalse();
                    Assertions.assertThat(testAlarm.getAgreeLiveRainAlarm()).isFalse();
                });

        Assertions.assertThat(foundAlarm.get().getMember()).isNotNull()
                .satisfies(foundMember -> {
                    Assertions.assertThat(foundMember.getId()).isEqualTo(savedAlarm.getMember().getId());
                    Assertions.assertThat(foundMember.getEmail()).isEqualTo(savedAlarm.getMember().getEmail());
                });

        Assertions.assertThat(foundAlarm.get().getSummaryAlarmTimes()).isEmpty();
    }

    @Test
    public void 필수_정보_생략했을_때_예외가_발생한다() {
        Member member = saveMember();
        AlarmRequestDTO alarmRequestDTO = AlarmRequestDTO.builder()
                .build();
        Assertions.assertThatThrownBy(() -> {
                    Alarm.createDefaultAlarm(member, alarmRequestDTO);
                })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fcmToken은 알람 생성에 필수 요소입니다.");

        entityManager.flush();
        entityManager.clear();

        List<Alarm> allAlarms = alarmRepository.findAll();
        Assertions.assertThat(allAlarms).isEmpty();
    }

    public Member saveMember() {
        Member member = Member.builder()
                .email("email")
                .nickname("nickname")
                .build();

        Member savedMember = entityManager.persistAndFlush(member);
        entityManager.clear();
        return savedMember;
    }



}
