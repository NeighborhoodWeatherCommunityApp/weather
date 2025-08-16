package org.pknu.weather.recomandation.repository;

import org.pknu.weather.recomandation.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long>, RecommendationCustomRepository {

    Optional<Recommendation> findByMemberIdAndPostId(Long memberId, Long postId);

}
