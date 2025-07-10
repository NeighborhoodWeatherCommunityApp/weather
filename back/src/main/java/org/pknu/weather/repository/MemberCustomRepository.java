package org.pknu.weather.repository;


import java.util.List;
import org.pknu.weather.domain.common.SummaryAlarmTime;
import org.pknu.weather.dto.AlarmMemberDTO;
import java.time.LocalDateTime;
import org.pknu.weather.domain.Member;

public interface MemberCustomRepository {
    List<Member> findMembersInactiveSince(LocalDateTime localDateTime);
    public List<AlarmMemberDTO> findMembersAndAlarmsByAlarmTime(SummaryAlarmTime summaryAlarmTime);

}
