package org.pknu.weather.post.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.location.repository.LocationRepository;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.repository.MemberRepository;
import org.pknu.weather.tag.enums.EnumTagMapper;
import org.pknu.weather.tag.repository.TagRepository;
import org.pknu.weather.tag.service.TagQueryService;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.dto.WeatherResponseDTO;
import org.pknu.weather.weather.feignclient.utils.ExtraWeatherApiUtils;
import org.pknu.weather.weather.feignclient.utils.WeatherFeignClientUtils;
import org.pknu.weather.weather.repository.ExtraWeatherRepository;
import org.pknu.weather.weather.repository.WeatherRepository;
import org.pknu.weather.weather.service.WeatherQueryService;
import org.pknu.weather.weather.service.WeatherService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagQueryServiceTest {
    WeatherFeignClientUtils weatherFeignClientUtils = mock(WeatherFeignClientUtils.class);
    WeatherRepository weatherRepository = mock(WeatherRepository.class);
    MemberRepository memberRepository = mock(MemberRepository.class);
    ExtraWeatherApiUtils mockextraweatherapiutils = mock(ExtraWeatherApiUtils.class);
    LocationRepository locationRepository = mock(LocationRepository.class);
    ExtraWeatherRepository extraWeatherRepository = mock(ExtraWeatherRepository.class);
    TagRepository tagRepository = mock(TagRepository.class);
    EnumTagMapper enumTagMapper = mock(EnumTagMapper.class);
    ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);

    WeatherService weatherService = spy(new WeatherService(
            weatherFeignClientUtils,
            weatherRepository,
            extraWeatherRepository,
            memberRepository,
            mockextraweatherapiutils,
            locationRepository)
    );

    WeatherQueryService weatherQueryService = spy(new WeatherQueryService(
            memberRepository,
            weatherRepository,
            weatherFeignClientUtils,
            applicationEventPublisher,
            new CacheManager() {
                @Override
                public Cache getCache(String name) {
                    return null;
                }

                @Override
                public Collection<String> getCacheNames() {
                    return null;
                }
            }
    ));

    TagQueryService tagQueryService = spy(new TagQueryService(
            tagRepository,
            memberRepository,
            weatherService,
            enumTagMapper,
            weatherQueryService
    ));

    @Test
    void 날씨_데이터가_없을때는_API를_호출하여_데이터를_받아와_tag를_반환합니다() {
        // given
        Member member = TestDataCreator.getBusanMember(1L);
        Location location = member.getLocation();
        LocalDateTime baseTime = TestDataCreator.getBaseTime();
        List<Weather> newForecast = TestDataCreator.getNewForecast(location, baseTime);
        WeatherResponseDTO.ExtraWeatherInfo extraWeatherInfo = TestDataCreator.getExtraWeatherInfo(baseTime);

        when(memberRepository.safeFindByEmail(member.getEmail())).thenReturn(member);
        when(weatherRepository.findWeatherByClosestPresentationTime(any(Location.class))).thenReturn(Optional.empty());

        doReturn(newForecast).when(weatherFeignClientUtils).getVillageShortTermForecast(location);
        doReturn(extraWeatherInfo).when(weatherService).extraWeatherInfo(member.getEmail(), location.getId());

        // when then
        assertDoesNotThrow(() -> {
            tagQueryService.getTagsWithSelectionStatus(member.getEmail());
        });

        verify(weatherQueryService, times(1)).getNearestWeatherForecastToNow(any(Location.class));
        verify(weatherFeignClientUtils, times(1)).getVillageShortTermForecast(any(Location.class));
    }

    @Test
    void 날씨_데이터가_존재할때는_외부_api를_호출하지_않습니다() {
        // given
        Member member = TestDataCreator.getBusanMember(1L);
        Location location = member.getLocation();
        LocalDateTime baseTime = TestDataCreator.getBaseTime();
        List<Weather> newForecast = TestDataCreator.getNewForecast(location, baseTime);
        WeatherResponseDTO.ExtraWeatherInfo extraWeatherInfo = TestDataCreator.getExtraWeatherInfo(baseTime);

        when(memberRepository.safeFindByEmail(member.getEmail())).thenReturn(member);
        when(weatherRepository.findWeatherByClosestPresentationTime(any(Location.class))).thenReturn(Optional.ofNullable(newForecast.get(0)));

        doReturn(newForecast).when(weatherFeignClientUtils).getVillageShortTermForecast(location);
        doReturn(extraWeatherInfo).when(weatherService).extraWeatherInfo(member.getEmail(), location.getId());

        // when then
        assertDoesNotThrow(() -> {
            tagQueryService.getTagsWithSelectionStatus(member.getEmail());
        });

        verify(weatherQueryService, times(1)).getNearestWeatherForecastToNow(any(Location.class));
        verify(weatherFeignClientUtils, times(0)).getVillageShortTermForecast(any(Location.class));
    }
}