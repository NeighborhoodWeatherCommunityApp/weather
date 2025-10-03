package org.pknu.weather.weather.repository;

import java.util.Set;
import org.pknu.weather.weather.ExtraWeather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

public interface ExtraWeatherRepository extends JpaRepository<ExtraWeather, Long> {

    Optional<ExtraWeather> findByLocationId(Long locationId);
    @Query("select ew from ExtraWeather ew where ew.location.id in :locationIds and ew.basetime > :fourHoursAgo")
    List<ExtraWeather> findExtraWeatherByLocations( @Param("locationIds") Set<Long> locationIds,
                                                    @Param("fourHoursAgo")LocalDateTime fourHoursAgo);


}
