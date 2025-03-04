package org.pknu.weather.repository;

import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.domain.Member;
import org.pknu.weather.exception.GeneralException;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

    @EntityGraph(attributePaths = {"location"})
    Optional<Member> findById(Long id);

    default Member safeFindById(Long id) {
        return findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
    }

    // true = location이 있다.
    default Boolean hasRegisteredLocation(String email) {
        Member member = safeFindByEmail(email);
        return member.getLocation() != null;
    }

    Optional<Member> findByNickname(String name);

    Optional<Member> findMemberByEmail(@Param("email") String email);

    @EntityGraph(attributePaths = {"location"})
    Optional<Member> findByEmail(String email);

    default Member safeFindByEmail(String email) {
        return findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
    }
}

