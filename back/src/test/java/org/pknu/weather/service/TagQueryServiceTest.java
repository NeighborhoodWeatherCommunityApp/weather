package org.pknu.weather.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.common.mapper.EnumTagMapper;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.dto.WeatherResponse;
import org.pknu.weather.feignClient.utils.ExtraWeatherApiUtils;
import org.pknu.weather.feignClient.utils.WeatherFeignClientUtils;
import org.pknu.weather.repository.*;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagQueryServiceTest {

    @Test
    void 날씨_데이터가_없을때는_API를_호출하여_데이터를_받아온후_tag를_반환합니다() {
        // given
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

        TagQueryService tagQueryService = spy(new TagQueryService(
                tagRepository,
                memberRepository,
                weatherRepository,
                weatherService,
                enumTagMapper,
                weatherFeignClientUtils,
                applicationEventPublisher
        ));

        Member member = TestDataCreator.getBusanMember(1L);
        Location location = member.getLocation();
        LocalDateTime baseTime = TestDataCreator.getBaseTime();
        List<Weather> newForecast = TestDataCreator.getNewForecast(location, baseTime);
        WeatherResponse.ExtraWeatherInfo extraWeatherInfo = TestDataCreator.getExtraWeatherInfo(baseTime);

        when(memberRepository.safeFindByEmail(member.getEmail())).thenReturn(member);
        when(weatherRepository.findByLocationClosePresentationTime(any(Location.class))).thenReturn(Optional.empty());

        doReturn(newForecast)
                .when(weatherFeignClientUtils).getVillageShortTermForecast(location);

        doReturn(extraWeatherInfo)
                .when(weatherService).extraWeatherInfo(member.getEmail(), location.getId());

        // when then
        assertDoesNotThrow(() -> {
            tagQueryService.getTagsWithSelectedOrNot(member.getEmail());
        });
    }
}