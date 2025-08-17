package org.pknu.weather.member.repository;

import java.util.Optional;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.exception.GeneralException;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository {

    @EntityGraph(attributePaths = {"location"})
    Optional<Member> findById(Long id);

    default Member safeFindById(Long id) {
        return findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
    }

    // true = location이 있다.
    default Boolean hasRegisteredLocation(Member member) {
        return member.getLocation() != null;
    }

    Optional<Member> findByNickname(String name);

    Optional<Member> findMemberByEmail(@Param("email") String email);

    @EntityGraph(attributePaths = {"location"})
    Optional<Member> findByEmail(String email);

    Optional<Member> findMemberWithLocationByEmail(@Param("email") String email);

    default Member safeFindByEmail(String email) {
        return findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
    }

    @EntityGraph(attributePaths = {"roles"})
    Optional<Member> findMemberWithRolesByEmail(@Param("email") String email);

}

