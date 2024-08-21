package org.pknu.weather.repository;

import org.pknu.weather.domain.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

}
