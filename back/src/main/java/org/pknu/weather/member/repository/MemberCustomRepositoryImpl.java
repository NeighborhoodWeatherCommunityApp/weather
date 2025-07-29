package org.pknu.weather.member.repository;

import static org.pknu.weather.member.entity.QMember.member;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.domain.QAlarm;
import org.pknu.weather.domain.common.SummaryAlarmTime;
import org.pknu.weather.dto.AlarmMemberDTO;

import java.time.LocalDateTime;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.entity.QMember;

@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<Member> findMembersInactiveSince(LocalDateTime localDateTime) {
        return jpaQueryFactory
                .selectFrom(member)
                .where(
                        member.updatedAt.before(localDateTime)
                )
                .fetch();
    }

    public List<AlarmMemberDTO> findMembersAndAlarmsByAlarmTime(SummaryAlarmTime time) {
        QAlarm alarm = QAlarm.alarm;
        QMember member = QMember.member;

        return jpaQueryFactory
                .select(Projections.fields(AlarmMemberDTO.class,
                        member.id,
                        member.location.id.as("locationId"),
                        alarm.fcmToken,
                        alarm.agreeTempAlarm,
                        alarm.agreePrecipAlarm,
                        alarm.agreeDustAlarm,
                        alarm.agreeUvAlarm,
                        alarm.agreeLiveRainAlarm
                ))
                .from(alarm)
                .join(alarm.member, member)
                .where(alarm.summaryAlarmTimes.contains(time))
                .fetch();
    }
}
