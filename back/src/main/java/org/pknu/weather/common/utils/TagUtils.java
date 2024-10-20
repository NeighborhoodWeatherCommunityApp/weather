package org.pknu.weather.common.utils;

import org.pknu.weather.domain.common.Sensitivity;
import org.pknu.weather.domain.tag.*;

import java.util.List;

public class TagUtils {

    private static final int VERY_HOT = 32;
    private static final int HOT = 27;
    private static final int LITTLE_HOT = 23;
    private static final int AVERAGE = 20;
    private static final int COOL = 17;
    private static final int LITTLE_COLD = 13;
    private static final int COLD = 8;

    public static RainTag rain2Text(Float rain) {
        if(rain == 0.0) return RainTag.NOTHING;
        else if(rain <= 1.0) return RainTag.ALMOST_NOTHING;
        else if(rain <= 3.0) return RainTag.VERY_WEAK;
        else if(rain <= 6.0) return RainTag.WEAK;
        else if(rain <= 10.0) return RainTag.AVERAGE;
        else if(rain <= 20.0) return RainTag.STRONG;
        else return RainTag.VERY_STRONG;
    }

    public static TemperatureTag tmp2Text(Integer tmp, Sensitivity sensitivity) {
        int adjustment = 0;
        int weight = 1;

        if(sensitivity == Sensitivity.HOT) {
            adjustment = -3;
        }

        if(sensitivity == Sensitivity.COLD) {
            adjustment = 3;
            weight = -1;
        }

        if(sensitivity == Sensitivity.NONE) {
            weight = 0;
        }

        if (tmp >= VERY_HOT + adjustment)
            return TemperatureTag.VERY_HOT;
        else if (tmp >= HOT + adjustment + weight)
            return TemperatureTag.HOT;
        else if (tmp >= LITTLE_HOT + adjustment + 2 * weight)
            return TemperatureTag.LITTLE_HOT;
        else if (tmp >= AVERAGE)
            return TemperatureTag.COMMON;
        else if (tmp >= COOL)
            return TemperatureTag.COOL;
        else if (tmp >= LITTLE_COLD + adjustment + 2 * weight)
            return TemperatureTag.LITTLE_COLD;
        else if (tmp >= COLD + adjustment + weight)
            return TemperatureTag.COLD;
        else
            return TemperatureTag.VERY_COLD;
    }

    public static String temperatureAndHumidityTag2Text(List<EnumTag> list) {

        HumidityTag humidityTag = null;
        TemperatureTag temperatureTag = null;

        for (EnumTag tag : list) {
            if(isTemperatureTag(tag)) {
                temperatureTag = (TemperatureTag) tag;
            }

            if(isHumidityTag(tag)) {
                humidityTag = (HumidityTag) tag;
            }
        }

        assert temperatureTag != null;
        assert humidityTag != null;

        String temperature = tag2Text(temperatureTag);
        String humidity = tag2Text(humidityTag);

        return humidity + ", " + temperature;
    }

    public static String tag2Text(EnumTag tag) {
        if(isTemperatureTag(tag)) return temperatureTag2Text(tag);
        else if(isWindTag(tag)) return windTag2Text(tag);
        else if(isHumidityTag(tag)) return humidityTag2Text(tag);
        else if(isSkyTag(tag)) return skyTag2Text(tag);
        else return dustTag2Text(tag);
    }

    private static String skyTag2Text(EnumTag skyTag) {
        assert skyTag instanceof SkyTag;
        return skyTag.toText();
    }

    private static String dustTag2Text(EnumTag dustTag) {
        assert dustTag instanceof DustTag;
        return "미세먼지 " + dustTag.toText();
    }

    private static String humidityTag2Text(EnumTag humidityTag) {
        assert humidityTag instanceof HumidityTag;
        return humidityTag.toText();
//        return switch (humidityTag.getCode()) {
//            case 1 -> "건조하고";
//            case 3 -> "약간 습하고";
//            case 4 -> "습하면서";
//            case 5 -> "엄청 습하고";
//            default -> "안 습하고";
//        };
    }

    private static String temperatureTag2Text(EnumTag temperatureTag) {
        assert temperatureTag instanceof TemperatureTag;
        return temperatureTag.toText();

//        return switch (temperatureTag.getCode()) {
//            case 1 -> "엄청 추움";
//            case 2 -> "추워요";
//            case 3 -> "조금 추워요";
//            case 4 -> "선선해요";
//            case 6 -> "따뜻해요";
//            case 7 -> "조금 따뜻해요";
//            case 8 -> "조금 더워요";
//            case 9 -> "더워요";
//            case 10 -> "엄청 더워요";
//            default -> "무난해요";
//        };
    }

    private static String windTag2Text(EnumTag windTag) {
        assert windTag instanceof WindTag;
        String wind = "바람 ";
        Integer code = windTag.getCode();
        return switch (code) {
            case 1 -> wind + windTag.findByCode(code).getText();
            case 3 -> wind + windTag.findByCode(code).getText();
            default -> wind + windTag.findByCode(code).getText();
        };
    }

    public static boolean isTempTagOrHumdiTag(EnumTag tag) {
        return isTemperatureTag(tag) || isHumidityTag(tag);
    }

    public static boolean isTemperatureTag(EnumTag tag) {
        return tag.getClass() == TemperatureTag.class;
    }

    public static boolean isHumidityTag(EnumTag tag) {
        return tag.getClass() == HumidityTag.class;
    }

    public static boolean isSkyTag(EnumTag tag) {
        return tag.getClass() == SkyTag.class;
    }

    public static boolean isDustTag(EnumTag tag) {
        return tag.getClass() == DustTag.class;
    }

    public static boolean isWindTag(EnumTag tag) {
        return tag.getClass() == WindTag.class;
    }
}
