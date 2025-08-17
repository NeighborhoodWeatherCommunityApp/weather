package org.pknu.weather.common;

import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.enums.Sensitivity;
import org.pknu.weather.post.entity.Post;
import org.pknu.weather.post.enums.PostType;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.dto.WeatherResponseDTO;
import org.pknu.weather.weather.enums.RainType;
import org.pknu.weather.weather.enums.SkyType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDataCreator {
    private static long locationIdx = 1;
    private static long memberIdx = 1;

    public static Map<LocalDateTime, Weather> getPastForecast(Location location, LocalDateTime baseTime) {
        // 현재 시각
        LocalDateTime presentTime = baseTime.plusHours(0);
        Map<LocalDateTime, Weather> weatherMap = new HashMap<>();

        // 3시간 전에 발표한 예보 만들기
        for (int i = 1; i <= 24; i++) {
            Weather weather = Weather.builder()
                    .basetime(baseTime)
                    .presentationTime(presentTime.plusHours(i))
                    .location(location)
                    .rainType(RainType.values()[(int) (Math.random() * RainType.values().length)])
                    .rain((float) (Math.random() * 10 + i))
                    .rainProb((int) (Math.random() * 100))
                    .temperature((int) (Math.random() * 30 + i))
                    .humidity((int) (Math.random() * 100))
                    .windSpeed(Math.random() * 10 + i)
                    .snowCover((float) (Math.random() * 5 + i))
                    .skyType(SkyType.values()[(int) (Math.random() * SkyType.values().length)])
                    .build();

            weatherMap.put(weather.getPresentationTime(), weather);
        }

        return weatherMap;
    }

    public static List<Weather> getNewForecast(Location location, LocalDateTime baseTime) {
        // 현재 시각
        LocalDateTime presentTime = baseTime.plusHours(0);
        List<Weather> weatherList = new ArrayList<>();

        // 3시간 전에 발표한 예보 만들기
        for (int i = 1; i <= 24; i++) {
            Weather weather = Weather.builder()
                    .basetime(baseTime)
                    .presentationTime(presentTime.plusHours(i))
                    .location(location)
                    .rainType(RainType.values()[(int) (Math.random() * RainType.values().length)])
                    .rain((float) (Math.random() * 10 + i))
                    .rainProb((int) (Math.random() * 100))
                    .temperature((int) (Math.random() * 30 + i))
                    .humidity((int) (Math.random() * 100))
                    .windSpeed(Math.random() * 10 + i)
                    .snowCover((float) (Math.random() * 5 + i))
                    .skyType(SkyType.values()[(int) (Math.random() * SkyType.values().length)])
                    .build();

            weatherList.add(weather);
        }

        return weatherList;
    }

    public static Member getBusanMember() {
        return Member.builder()
                .location(getBusanLocation())
                .email("test@naver.com")
                .profileImage("http://test.png")
                .sensitivity(Sensitivity.HOT)
                .nickname("busan member")
                .build();
    }

    public static LocalDateTime getBaseTime() {
        return DateTimeFormatter.getBaseLocalDateTime(LocalDateTime.now().withMinute(0).withSecond(0).withNano(0));
    }

    public static Member getBusanMember(Long memberId) {
        return Member.builder()
                .id(memberId)
                .location(getBusanLocation())
                .email("test@naver.com")
                .profileImage("http://test.png")
                .sensitivity(Sensitivity.HOT)
                .nickname("busan member")
                .build();
    }

    public static WeatherResponseDTO.ExtraWeatherInfo getExtraWeatherInfo(LocalDateTime baseTime) {
        return WeatherResponseDTO.ExtraWeatherInfo.builder()
                .baseTime(baseTime)
                .o3Grade(1)
                .uvGrade(1)
                .pm10Grade(1)
                .pm10Value(1)
                .pm25Value(1)
                .uvGradePlus3(1)
                .uvGradePlus9(1)
                .uvGradePlus6(1)
                .uvGradePlus15(1)
                .uvGradePlus18(1)
                .uvGradePlus21(1)
                .uvGradePlus12(1)
                .o3Grade(1)
                .build();
    }

    public static Member getBusanMember(String nickname) {
        return Member.builder()
                .location(getBusanLocation())
                .email(nickname + "@naver.com")
                .profileImage("http://test.png")
                .sensitivity(Sensitivity.HOT)
                .nickname(nickname)
                .build();
    }

    public static Member getBusanMember(Long id, String nickname) {
        return Member.builder()
                .id(id)
                .location(getBusanLocation())
                .email(nickname + "@naver.com")
                .profileImage("http://test.png")
                .sensitivity(Sensitivity.HOT)
                .nickname(nickname)
                .build();
    }

    public static Member getMember(Long id, String nickname, Location location) {
        return Member.builder()
                .id(id)
                .location(location)
                .email(nickname + "@naver.com")
                .profileImage("http://test.png")
                .sensitivity(Sensitivity.HOT)
                .nickname(nickname)
                .build();
    }

    public static Post getPost(Member member) {
        try {
            Thread.sleep(1);
            return Post.builder()
                    .location(member.getLocation())
                    .member(member)
                    .content("content")
                    .postType(PostType.WEATHER)
                    .build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 인터럽트 상태 복구
            throw new RuntimeException(e); // 또는 로깅 후 처리
        }
    }

    public static LocalDateTime getLocalDateTime() {
        return LocalDateTime.now()
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    public static LocalDateTime getLocalDateTimePlusHours(int hours) {
        return getLocalDateTime().plusHours(hours);
    }

    public static Location getBusanLocation() {
        return Location.builder()
                .city("시군구")
                .province("부산광역시" + locationIdx++)
                .street("읍면동")
                .latitude(TestGlobalParams.BusanGeometry.LATITUDE)
                .longitude(TestGlobalParams.BusanGeometry.LONGITUDE)
                .build();
    }

    public static Location getSeoulLocation() {
        return Location.builder()
                .city("시군구")
                .province("서울광역시" + locationIdx++)
                .street("읍면동")
                .latitude(TestGlobalParams.SeoulGeometry.LATITUDE)
                .longitude(TestGlobalParams.SeoulGeometry.LONGITUDE)
                .build();
    }

}
