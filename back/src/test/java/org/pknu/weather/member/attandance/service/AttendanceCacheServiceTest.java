package org.pknu.weather.member.attandance.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.config.EmbeddedRedisConfig;
import org.pknu.weather.config.TestAsyncConfig;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.member.attandance.entity.Attendance;
import org.pknu.weather.member.attandance.repository.AttendanceRepository;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import({EmbeddedRedisConfig.class, TestAsyncConfig.class})
@SpringBootTest
@Transactional
class AttendanceCacheServiceTest {
    @Autowired
    AttendanceCacheService attendanceCacheService;

    @SpyBean
    AttendanceRepository attendanceRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    void clearRedis() {
        Objects.requireNonNull(stringRedisTemplate.getConnectionFactory())
                .getConnection()
                .serverCommands()
                .flushAll(); // 모든 데이터 삭제
    }

    @Test
    void 출석체크_정상_테스트() {
        // given
        Member busanMember = memberRepository.saveAndFlush(TestDataCreator.getBusanMember());

        // when
        attendanceCacheService.checkIn(busanMember.getEmail());

        // then
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    void 중복_출석_테스트() {
        Member member = memberRepository.saveAndFlush(TestDataCreator.getBusanMember());
        String key = "attendance:" + LocalDate.now();
        int offset = Math.toIntExact(member.getId());

        // Redis에 비트 세팅 (이전값은 0→1)
        stringRedisTemplate.opsForValue().setBit(key, member.getId(), true);

        assertThrows(GeneralException.class, () -> attendanceCacheService.checkIn(member.getEmail()));

        // 첫 체크인 시도만 저장됐는지(=중복 시 저장 안됨) 검증
        verify(attendanceRepository, times(0)).save(any(Attendance.class));
    }
}
