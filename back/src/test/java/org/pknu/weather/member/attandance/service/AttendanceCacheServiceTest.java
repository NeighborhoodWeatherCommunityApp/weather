package org.pknu.weather.member.attandance.service;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import({EmbeddedRedisConfig.class, TestAsyncConfig.class})
@SpringBootTest
@Transactional
@Slf4j
class AttendanceCacheServiceTest {
    @Autowired
    AttendanceCacheService attendanceCacheService;

    @SpyBean
    AttendanceRepository attendanceRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    EntityManager em;

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
        Member member = memberRepository.saveAndFlush(TestDataCreator.getBusanMember());
        LocalDate date = LocalDate.now();
        String key = "attendance:" + date;

        // when
        attendanceCacheService.checkIn(member.getEmail(), date);

        // then
        verify(attendanceRepository, times(1)).save(any(Attendance.class));

        // DB 정합성 확인
        Attendance attendance = attendanceRepository.findAll().get(0);
        Assertions.assertThat(attendance.getDate()).isEqualTo(date);
        Assertions.assertThat(attendance.isCheckedIn()).isTrue();

        Boolean result = stringRedisTemplate.opsForValue().getBit(key, member.getId());
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    void 중복_출석_테스트() {
        // given
        Member member = memberRepository.saveAndFlush(TestDataCreator.getBusanMember());
        LocalDate date = LocalDate.now();
        String key = "attendance:" + date;

        attendanceCacheService.checkIn(member.getEmail(), date);    // 첫 출석 체크
        assertThrows(GeneralException.class, () -> attendanceCacheService.checkIn(member.getEmail(), date)); // 중복 출석 체크

        // 첫 체크인 시도만 저장됐는지 검증
        verify(attendanceRepository, times(1)).save(any(Attendance.class));

        // DB 정합성 확인
        Attendance attendance = attendanceRepository.findAll().get(0);
        Assertions.assertThat(attendance.getDate()).isEqualTo(date);
        Assertions.assertThat(attendance.isCheckedIn()).isTrue();

        Boolean result = stringRedisTemplate.opsForValue().getBit(key, member.getId());
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    void 만약_캐시서버_재시작으로_데이터가_유실되었을때_중복으로_출석을_허용하지_않는다() {
        // given
        Member member = memberRepository.saveAndFlush(TestDataCreator.getBusanMember());
        LocalDate date = LocalDate.now();

        // when
        // 첫 출석체크
        attendanceCacheService.checkIn(member.getEmail(), date);

        // 데이터 유실 상황
        clearRedis();

        // 두 번째 출석 체크
        attendanceCacheService.checkIn(member.getEmail(), date);
        em.clear(); // 예외 처리로 인해 영속성 컨텍스트에 id가 없는 attendance 엔티티가 존재하기 떄문에 clear

        // then
        long count = attendanceRepository.count();
        assertEquals(1, count);
    }

    @Test
    void 출석체크_속도_테스트_lua스크립트_사용() {
        Member member = memberRepository.saveAndFlush(TestDataCreator.getBusanMember());
        LocalDate date = LocalDate.now();

        long start = System.currentTimeMillis();
        attendanceCacheService.checkIn(member.getEmail(), date);
        long end = System.currentTimeMillis();

        log.info("time={}ms", end - start);
    }

    @Test
    void 출석체크_속도_테스트_블럭lock_사용() {
        Member member = memberRepository.saveAndFlush(TestDataCreator.getBusanMember());
        LocalDate date = LocalDate.now();

        long start = System.currentTimeMillis();
        attendanceCacheService.checkInSynchronized(member.getEmail(), date);
        long end = System.currentTimeMillis();

        log.info("time={}ms", end - start);
    }
}
