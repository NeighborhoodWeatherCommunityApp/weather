package org.pknu.weather.repository;

import org.pknu.weather.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
