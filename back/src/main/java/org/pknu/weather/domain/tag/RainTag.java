package org.pknu.weather.domain.tag;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.domain.common.RainType;
import org.pknu.weather.dto.TotalWeatherDto;
import org.pknu.weather.dto.TotalWeatherDto.WeatherDto;
import org.pknu.weather.exception.GeneralException;

@Getter
@RequiredArgsConstructor

public enum RainTag implements EnumTag {
    NOTHING("", "강수 없음", 1),
    LIGHT_RAIN("", "약한 비", 2),
    MODERATE_RAIN("", "보통 비", 3),
    HEAVY_RAIN("", "강한 비", 4),
    EXTREME_RAIN("", "매우 강한 비", 5),

    LIGHT_RAIN_AND_SNOW("", "약한 눈/비", 6),
    MODERATE_RAIN_AND_SNOW("", "보통 눈/비", 7),
    HEAVY_RAIN_AND_SNOW("", "강한 눈/비", 8),
    EXTREME_RAIN_AND_SNOW("매우", "강한 눈/비", 9),

    LIGHT_SNOW("", "약한 눈", 10),
    MODERATE_SNOW("", "보통 눈", 11),
    HEAVY_SNOW("", "강한 눈", 12),

    SHOWER("", "소나기", 20),
    ;

    private final String adverb;
    private final String text;
    private final Integer code;

    @Override
    public EnumTag findByCode(int code) {
        return Arrays.stream(values())
                .filter(e -> e.code.equals(code))
                .findAny()
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST));
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public EnumTag weatherValueToTag(TotalWeatherDto totalWeatherDto) {
        return rainTypeToRainTag(totalWeatherDto.getWeatherDto());
    }

    private static RainTag rainTypeToRainTag(WeatherDto weatherDto) {
        Float rain = weatherDto.getRain();
        Float snowCover = weatherDto.getSnowCover();
        RainType rainType = weatherDto.getRainType();

        switch (rainType) {
            case RAIN -> {
                return rainToRainTag(rain);
            }
            case RAIN_AND_SNOW -> {
                return rainAndSnowToRainTag(rain);
            }
            case SNOW -> {
                return snowTORainTag(snowCover);
            }
            case SHOWER -> {
                return RainTag.SHOWER;
            }
            default -> {
                return RainTag.NOTHING;
            }
        }
    }

    private static RainTag snowTORainTag(Float snowCover) {
        if (snowCover == 0.0) {
            return RainTag.NOTHING;
        } else if (snowCover <= 1.0) {
            return RainTag.LIGHT_SNOW;
        } else if (snowCover <= 3.0) {
            return RainTag.MODERATE_SNOW;
        } else {
            return RainTag.HEAVY_SNOW;
        }
    }

    private static RainTag rainAndSnowToRainTag(Float rain) {
        if (rain == 0.0) {
            return RainTag.NOTHING;
        } else if (rain <= 3.0) {
            return RainTag.LIGHT_RAIN_AND_SNOW;
        } else if (rain <= 15.0) {
            return RainTag.MODERATE_RAIN_AND_SNOW;
        } else {
            return RainTag.EXTREME_RAIN_AND_SNOW;
        }
    }

    private static RainTag rainToRainTag(Float rain) {
        if (rain == 0.0) {
            return RainTag.NOTHING;
        } else if (rain <= 3.0) {
            return RainTag.LIGHT_RAIN;
        } else if (rain <= 15.0) {
            return RainTag.MODERATE_RAIN;
        } else {
            return RainTag.EXTREME_RAIN;
        }
    }
}
