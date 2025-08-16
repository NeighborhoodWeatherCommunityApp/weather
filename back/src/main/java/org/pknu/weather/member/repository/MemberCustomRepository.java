package org.pknu.weather.member.repository;


import java.util.List;
import org.pknu.weather.alarm.enums.SummaryAlarmTime;
import org.pknu.weather.alarm.dto.AlarmMemberDTO;
import java.time.LocalDateTime;
import org.pknu.weather.member.entity.Member;

public interface MemberCustomRepository {
    List<Member> findMembersInactiveSince(LocalDateTime localDateTime);
    public List<AlarmMemberDTO> findMembersAndAlarmsByAlarmTime(SummaryAlarmTime summaryAlarmTime);

}
