package org.pknu.weather.post.tag.utils;

import java.util.List;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.member.enums.Sensitivity;
import org.pknu.weather.post.tag.enums.DustTag;
import org.pknu.weather.post.tag.enums.EnumTag;
import org.pknu.weather.post.tag.enums.HumidityTag;
import org.pknu.weather.post.tag.enums.RainTag;
import org.pknu.weather.post.tag.enums.SkyTag;
import org.pknu.weather.post.tag.enums.TemperatureTag;
import org.pknu.weather.post.tag.enums.WindTag;
import org.pknu.weather.weather.dto.TotalWeatherDto;

public class TagUtils {

    private static final int VERY_HOT = 32;
    private static final int HOT = 27;
    private static final int LITTLE_HOT = 23;
    private static final int AVERAGE = 20;
    private static final int COOL = 17;
    private static final int LITTLE_COLD = 13;
    private static final int COLD = 8;

    public static EnumTag rainType2RainTag(Weather weather) {
        RainTag rainTag = RainTag.NOTHING;
        return rainTag.weatherValueToTag(new TotalWeatherDto(weather));
    }

    public static TemperatureTag tmp2TemperatureTag(Integer tmp, Sensitivity sensitivity) {
        int adjustment = 0;
        int weight = 1;

        if (sensitivity == Sensitivity.HOT) {
            adjustment = -3;
        }

        if (sensitivity == Sensitivity.COLD) {
            adjustment = 3;
            weight = -1;
        }

        if (sensitivity == Sensitivity.NONE) {
            weight = 0;
        }

        if (tmp >= VERY_HOT + adjustment) {
            return TemperatureTag.VERY_HOT;
        } else if (tmp >= HOT + adjustment + weight) {
            return TemperatureTag.HOT;
        } else if (tmp >= LITTLE_HOT + adjustment + 2 * weight) {
            return TemperatureTag.LITTLE_HOT;
        } else if (tmp >= AVERAGE) {
            return TemperatureTag.COMMON;
        } else if (tmp >= COOL) {
            return TemperatureTag.COOL;
        } else if (tmp >= LITTLE_COLD + adjustment + 2 * weight) {
            return TemperatureTag.LITTLE_COLD;
        } else if (tmp >= COLD + adjustment + weight) {
            return TemperatureTag.COLD;
        } else {
            return TemperatureTag.VERY_COLD;
        }
    }

    public static String temperatureAndHumidityTag2TemperatureTag(List<EnumTag> list) {
        HumidityTag humidityTag = null;
        TemperatureTag temperatureTag = null;

        for (EnumTag tag : list) {
            if (isTemperatureTag(tag)) {
                temperatureTag = (TemperatureTag) tag;
            }

            if (isHumidityTag(tag)) {
                humidityTag = (HumidityTag) tag;
            }
        }

        assert temperatureTag != null;
        assert humidityTag != null;

        String temperature = temperatureTag.toText();
        String humidity = humidityTag.toText();

        return humidity + ", " + temperature;
    }

    public static boolean isTempTagOrHumdiTag(EnumTag tag) {
        return isTemperatureTag(tag) || isHumidityTag(tag);
    }

    public static boolean isTemperatureTag(EnumTag tag) {
        return tag instanceof TemperatureTag;
    }

    public static boolean isHumidityTag(EnumTag tag) {
        return tag instanceof HumidityTag;
    }

    public static boolean isSkyTag(EnumTag tag) {
        return tag instanceof SkyTag;
    }

    public static boolean isDustTag(EnumTag tag) {
        return tag instanceof DustTag;
    }

    public static boolean isWindTag(EnumTag tag) {
        return tag instanceof WindTag;
    }
}
