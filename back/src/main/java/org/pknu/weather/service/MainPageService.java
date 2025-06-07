package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.dto.PostResponse;
import org.pknu.weather.dto.TagDto;
import org.pknu.weather.dto.WeatherResponse;
import org.pknu.weather.dto.converter.WeatherResponseConverter;
import org.pknu.weather.feignClient.utils.WeatherFeignClientUtils;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 메인페이지에서 사용되는 API를 위한 서비스 즉, 화면에 맞춰진 로직을 관리한다. 해당 서비스는 서비스를 의존할 수 있다. 단 핵심 비즈니스 로직만 의존한다. 서비스를 참조하는 서비스를 한 곳으로 몰아서
 * 서비스간 순환 참조를 방지한다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainPageService {
    private final MemberRepository memberRepository;
    private final WeatherService weatherService;
    private final WeatherQueryService weatherQueryService;
    private final LocationRepository locationRepository;
    private final PostQueryService postQueryService;
    private final TagQueryService tagQueryService;
    private final WeatherFeignClientUtils weatherFeignClientUtils;

    /**
     * 메인 페이지에 날씨와 관련된 데이터를 반환한다. 만약 해당 지역의 날씨의 갱신 시간이 지났다면 갱신을 시도하고 반환한다. 만약 해당 지역의 날씨 정보가 없다면 저장하고 반환한다.
     *
     * @param email
     * @return
     */
    public WeatherResponse.MainPageWeatherData getWeatherInfo(String email, Long locationId) {
        Member member = memberRepository.safeFindByEmail(email);
        Location location = resolveLocation(member, locationId);

        List<Weather> weatherList = createWeatherIfRequired(location, member);
        if (weatherList != null) {
            return WeatherResponseConverter.toMainPageWeatherData(weatherList, member);
        }

        updateWeatherIfRequired(location);
        weatherList = weatherService.getWeathers(location.getId());

        return WeatherResponseConverter.toMainPageWeatherData(weatherList, member);
    }

    private Location resolveLocation(Member member, Long locationId) {
        return locationId != null
                ? locationRepository.safeFindById(locationId)
                : member.getLocation();
    }

    private List<Weather> createWeatherIfRequired(Location location, Member member) {
        if (!weatherQueryService.weatherHasBeenCreated(location)) {
            List<Weather> weatherList = weatherFeignClientUtils.getVillageShortTermForecast(location);
            weatherService.bulkSaveWeathersAsync(location.getId(), weatherList);
            return weatherList;
        }
        return null;
    }

    private void updateWeatherIfRequired(Location location) {
        if (!weatherQueryService.weatherHasBeenUpdated(location)) {
            weatherService.bulkUpdateWeathersAsync(location.getId());
        }
    }

    /**
     * 사용자의 지역에서 가장 최근에 작성된 글 5개를 반환한다.
     *
     * @param email
     * @return
     */
    public List<PostResponse.Post> getLatestPostList(String email) {
        return postQueryService.getLatestPostList(email);
    }

    public List<TagDto.SimpleTag> getMostSelectedTags(String email) {
        return tagQueryService.getMostSelectedTags(email);
    }

    public WeatherResponse.SimpleRainInformation getSimpleRainInfo(String email) {
        return weatherQueryService.getSimpleRainInfo(email);
    }
}