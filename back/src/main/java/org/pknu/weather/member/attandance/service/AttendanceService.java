package org.pknu.weather.member.attandance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.member.attandance.entity.Attendance;
import org.pknu.weather.member.attandance.repository.AttendanceRepository;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.event.AttendanceCheckedEvent;
import org.pknu.weather.member.repository.MemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
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

        eventPublisher.publishEvent(new AttendanceCheckedEvent(member.getEmail()));
    }

    @Async
    @Transactional
    public void checkInAsync(Member member, LocalDate date) {
        Attendance attendance = Attendance.builder()
                .date(date)
                .member(member)
                .build();

        attendance.checkIn();
        attendanceRepository.save(attendance);

        eventPublisher.publishEvent(new AttendanceCheckedEvent(member.getEmail()));
    }
}
