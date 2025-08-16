package org.pknu.weather.member.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.pknu.weather.alarm.dto.AlarmMemberDTO;
import org.pknu.weather.alarm.entity.Alarm;
import org.pknu.weather.alarm.enums.SummaryAlarmTime;
import org.pknu.weather.config.DataJpaTestConfig;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@Import(DataJpaTestConfig.class)
@DataJpaTest
class MemberCustomRepositoryImplTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private MemberRepository memberRepository;

    @ParameterizedTest
    @MethodSource("provideMatchedSummaryAlarmTimes")
    void 시간대에_맞는_알람만_조회된다(SummaryAlarmTime expectedAlarmTime, Set<SummaryAlarmTime> memberAlarmTimes) {

        // Given
        saveMemberAndAlarm("test@example.com", "test-fcm-token", memberAlarmTimes);


        // When
        List<AlarmMemberDTO> result = memberRepository.findMembersAndAlarmsByAlarmTime(expectedAlarmTime);

        // Then
        assertThat(result)
                .hasSize(1)
                .extracting(AlarmMemberDTO::getFcmToken)
                .containsExactly("test-fcm-token");
    }

    @ParameterizedTest
    @MethodSource("provideMismatchedSummaryAlarmTimes")
    void 시간대가_다르면_알람이_조회되지_않는다(SummaryAlarmTime expectedAlarmTime, Set<SummaryAlarmTime> memberAlarmTimes) {
        // Given
        saveMemberAndAlarm("mismatch@example.com", "mismatch-fcm-token", memberAlarmTimes);

        // When
        List<AlarmMemberDTO> result = memberRepository.findMembersAndAlarmsByAlarmTime(expectedAlarmTime);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void 같은_시간대에_여러명의_알람이_조회된다() {

        // Given
        saveMemberAndAlarm("user1@example.com", "fcm-token-1", Collections.singleton(SummaryAlarmTime.MORNING));
        saveMemberAndAlarm("user2@example.com", "fcm-token-2", Set.of(SummaryAlarmTime.MORNING,SummaryAlarmTime.AFTERNOON));
        saveMemberAndAlarm("user3@example.com", "fcm-token-3", Collections.singleton(SummaryAlarmTime.MORNING));


        // When
        List<AlarmMemberDTO> result = memberRepository.findMembersAndAlarmsByAlarmTime(SummaryAlarmTime.MORNING);

        // Then
        assertThat(result)
                .hasSize(3)
                .extracting(AlarmMemberDTO::getFcmToken)
                .containsExactlyInAnyOrder("fcm-token-1", "fcm-token-2", "fcm-token-3");
    }

    private void saveMemberAndAlarm(String email, String fcmToken, Set<SummaryAlarmTime> alarmTime) {
        Member member = Member.builder()
                .email(email)
                .nickname("User " + email)
                .build();

        Alarm alarm = Alarm.builder()
                .member(member)
                .fcmToken(fcmToken)
                .agreeTempAlarm(true)
                .agreePrecipAlarm(true)
                .agreeDustAlarm(true)
                .agreeUvAlarm(true)
                .agreeLiveRainAlarm(true)
                .summaryAlarmTimes(alarmTime)
                .build();

        testEntityManager.persistAndFlush(member);
        testEntityManager.persistAndFlush(alarm);

    }

    static Stream<Arguments> provideMatchedSummaryAlarmTimes() {
        return Stream.of(
                arguments(SummaryAlarmTime.MORNING, Set.of(SummaryAlarmTime.MORNING,SummaryAlarmTime.AFTERNOON)),
                arguments(SummaryAlarmTime.AFTERNOON, Set.of(SummaryAlarmTime.AFTERNOON)),
                arguments(SummaryAlarmTime.EVENING, Set.of(SummaryAlarmTime.EVENING,SummaryAlarmTime.AFTERNOON))
        );
    }

    static Stream<Arguments> provideMismatchedSummaryAlarmTimes() {
        return Stream.of(
                arguments(SummaryAlarmTime.MORNING, Set.of(SummaryAlarmTime.AFTERNOON,SummaryAlarmTime.EVENING)),
                arguments(SummaryAlarmTime.AFTERNOON,Set.of(SummaryAlarmTime.EVENING,SummaryAlarmTime.MORNING)),
                arguments(SummaryAlarmTime.EVENING,Set.of(SummaryAlarmTime.MORNING))
        );
    }
}
