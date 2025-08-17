package org.pknu.weather.location.repository;

import java.util.List;

public interface LocationCustomRepository {
    List<Long> findLocationIdsWithRecentlyUpdatedWeather(Integer limitSize);
}
