package org.pknu.weather.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Attendance;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.exp.ExpEvent;
import org.pknu.weather.repository.AttendanceRepository;
import org.pknu.weather.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final MemberRepository memberRepository;
    private final ExpRewardService expRewardService;
    private final AttendanceRepository attendanceRepository;

    @Transactional
    public void checkIn(String email) {
        Member member = memberRepository.safeFindByEmail(email);

        Attendance attendance = Attendance.builder()
                .date(LocalDate.now())
                .member(member)
                .build();

        attendance.checkIn();
        attendanceRepository.save(attendance);

        // TODO: 이벤트 방식으로 변경
        expRewardService.rewardExp(member.getEmail(), ExpEvent.ATTENDANCE);
    }
}
