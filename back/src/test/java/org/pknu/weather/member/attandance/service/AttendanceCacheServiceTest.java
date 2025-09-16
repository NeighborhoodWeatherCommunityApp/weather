package org.pknu.weather.member.attandance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.config.EmbeddedRedisConfig;
import org.pknu.weather.config.RedisConfig;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.member.attandance.entity.Attendance;
import org.pknu.weather.member.attandance.repository.AttendanceRepository;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@Import({EmbeddedRedisConfig.class, RedisConfig.class})
@SpringBootTest
class AttendanceCacheServiceTest {
    @Autowired
    AttendanceCacheService attendanceCacheService;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void clearRedis() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory())
                .getConnection()
                .serverCommands()
                .flushAll(); // 모든 데이터 삭제
    }

    @Test
    @Transactional
    void 출석체크_정상_테스트() {
        // given
        Member busanMember = memberRepository.save(TestDataCreator.getBusanMember(1L));

        // when
        attendanceCacheService.checkIn(busanMember.getEmail());

        // then
        await().until(() -> attendanceRepository.count() == 1);
        List<Attendance> attendanceList = attendanceRepository.findAll();
        assertThat(attendanceList.get(1).getMember().getEmail()).isEqualTo(busanMember.getEmail());
    }

    @Test
    @Transactional
    void 중복_출석_테스트() {
        // given
        Member busanMember = memberRepository.save(TestDataCreator.getBusanMember(1L));
        LocalDate date = LocalDate.now();
        attendanceRepository.checkIn(String.format("attendance:%s", date.toString()), busanMember.getId());

        // when
        assertThrows(GeneralException.class, () -> attendanceCacheService.checkIn(busanMember.getEmail()));
    }
}