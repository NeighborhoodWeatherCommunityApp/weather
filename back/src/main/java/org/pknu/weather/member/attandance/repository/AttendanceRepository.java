package org.pknu.weather.member.attandance.repository;

import org.pknu.weather.member.attandance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>, AttendanceCacheRepository {
    @Modifying
    @Transactional
    @Query(value = """
      INSERT INTO attendance (date, member_id, checked_in, created_at, updated_at)
      VALUES (:date, :memberId, true, NOW(), NOW())
      ON DUPLICATE KEY UPDATE
        checked_in = VALUES(checked_in),
        updated_at = NOW()
      """, nativeQuery = true)
    int upsertAttendance(@Param("date") LocalDate date, @Param("memberId") Long memberId);
}
