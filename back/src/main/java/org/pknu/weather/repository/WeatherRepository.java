package org.pknu.weather.repository;

import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WeatherRepository extends JpaRepository<Weather, Long> {
    @Query("select w from Weather w join fetch w.location l where w.presentationTime >= now()")
    List<Weather> findAllGreaterThanNowAndWithLocation();
}