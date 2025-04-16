package org.pknu.weather.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.pknu.weather.domain.Member;

public interface MemberCustomRepository {
    List<Member> findMembersInactiveSince(LocalDateTime localDateTime);
}

