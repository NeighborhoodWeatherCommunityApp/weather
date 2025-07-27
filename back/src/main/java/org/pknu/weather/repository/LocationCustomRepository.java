package org.pknu.weather.repository;

import java.util.List;

public interface LocationCustomRepository {
    List<Long> findLocationIdsWithRecentlyUpdatedWeather(Integer limitSize);
}
