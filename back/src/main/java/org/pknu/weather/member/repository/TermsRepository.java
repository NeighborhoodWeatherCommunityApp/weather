package org.pknu.weather.member.repository;

import org.pknu.weather.member.entity.Terms;
import org.pknu.weather.member.enums.TermsType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermsRepository extends JpaRepository<Terms, Long> {

    Terms findByTermsType(TermsType termsType);
}
