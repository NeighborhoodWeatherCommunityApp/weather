package org.pknu.weather.weather.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.pknu.weather.domain.Location;
import org.pknu.weather.weather.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WeatherRepository extends JpaRepository<Weather, Long>, WeatherCustomRepository {
    @Query("select w "
            + "from Weather w "
            + "join fetch w.location "
            + "where w.location.id = :locationId "
            + "and w.presentationTime >= now() "
            + "and w.presentationTime < :end"
    )
    List<Weather> findAllWithLocation(@Param("locationId") Long locationId, @Param("end") LocalDateTime end);

    @Modifying
    @Query("delete from Weather w where w.presentationTime < now()")
    void bulkDeletePastWeathers();

    void deleteAllByLocation(Location location);
}
