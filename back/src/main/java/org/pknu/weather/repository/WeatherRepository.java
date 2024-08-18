package org.pknu.weather.repository;

import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface WeatherRepository extends JpaRepository<Weather, Long> {
    @Query("select w from Weather w join fetch w.location where w.location = :location and w.presentationTime >= now() and w.presentationTime < :end")
    List<Weather> findAllWithLocation(Location location, LocalDateTime end);

    @Modifying
    @Query("delete from Weather w where w.presentationTime < now()")
    void bulkDeletePastWeathers();
}
