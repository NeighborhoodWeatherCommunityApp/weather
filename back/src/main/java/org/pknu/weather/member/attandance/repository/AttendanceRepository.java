package org.pknu.weather.member.attandance.repository;

import org.pknu.weather.member.attandance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
