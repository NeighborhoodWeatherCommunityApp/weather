package org.pknu.weather.repository;


import static org.pknu.weather.domain.QMember.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.domain.Member;

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
}

