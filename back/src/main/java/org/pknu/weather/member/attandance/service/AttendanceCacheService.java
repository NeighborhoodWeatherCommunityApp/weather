package org.pknu.weather.member.attandance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.member.attandance.repository.AttendanceRepository;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.repository.MemberRepository;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceCacheService {

    private final MemberRepository memberRepository;
    private final AttendanceService attendanceService;
    private final AttendanceRepository attendanceRepository;

    public void checkInSynchronized(String email) {
        Member member = memberRepository.safeFindByEmail(email);
        LocalDate date = LocalDate.now();

        synchronized (this) {
            boolean result = attendanceRepository.checkIn(getKey(date), member.getId());
            postCheckIn(result, member, date);
        }
    }

    public void checkIn(String email) {
        Member member = memberRepository.safeFindByEmail(email);
        LocalDate date = LocalDate.now();

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setScriptText(
                "return redis.call('SETBIT', KEYS[1], tonumber(ARGV[1]), 1)"
        );

        boolean result = attendanceRepository.checkIn(script, getKey(date), member.getId());
        postCheckIn(result, member, date);
    }

    private void postCheckIn(Boolean result, Member member, LocalDate date) {
        if (result) {
            throw new GeneralException(ErrorStatus._DUPLICATED_ATTENDANCE);
        } else {
            attendanceService.checkInAsync(member, date);
        }
    }

    private String getKey(LocalDate date) {
        return String.format("attendance:%s", date.toString());
    }
}
