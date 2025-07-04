package org.pknu.weather.repository;


import java.util.List;
import java.util.Optional;
import org.pknu.weather.domain.Alarm;
import org.pknu.weather.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Optional<Alarm> findByFcmTokenAndMember(String fcmToken, Member member);

    @EntityGraph(attributePaths = {"summaryAlarmTimes"})
    @Query("SELECT a FROM Alarm a WHERE a.fcmToken = :fcmToken")
    Optional<Alarm> findByfcmTokenWithSummaryAlarmTimes(@Param("fcmToken") String fcmToken);


    @Query("SELECT a.fcmToken " +
            "FROM Alarm a JOIN a.member m JOIN m.location l " +
            "WHERE l.id = :locationId AND a.agreeLiveRainAlarm = true")
    List<String> findLiveRainAlarmInfo(@Param("locationId") Long locationId);

}
