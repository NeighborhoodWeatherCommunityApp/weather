package org.pknu.weather.repository;

import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.exception.GeneralException;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @EntityGraph(attributePaths = { "location" })
    default Member safeFindById(Long id) {
        return findById(id).orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
    }
}
