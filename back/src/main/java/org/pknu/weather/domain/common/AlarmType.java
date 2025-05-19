package org.pknu.weather.domain.common;

import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public enum AlarmType {

    WEATHER_SUMMARY("날씨 요약 알림", Void.class),
    RAIN_ALERT("실시간 비 알림", Void.class),
    WEATHER_UPDATE("날씨 데이터 업데이트", Void.class),
    TEST_WEATHER_SUMMARY("날씨 요약 알림 테스트", Map.class);

    private final String description;
    private final Class<?> argumentType;

}
