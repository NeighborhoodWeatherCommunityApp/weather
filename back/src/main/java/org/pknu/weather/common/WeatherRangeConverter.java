package org.pknu.weather.common;

import org.pknu.weather.domain.common.RainTag;
import org.pknu.weather.domain.common.Sensitivity;
import org.pknu.weather.domain.common.TemperatureTag;

public class WeatherRangeConverter {
    private static final int VERY_HOT = 33;
    private static final int HOT = 28;
    private static final int LITTLE_HOT = 23;
    private static final int AVERAGE = 20;
    private static final int COOL = 17;
    private static final int LITTLE_COLD = 12;
    private static final int COLD = 5;

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
            return TemperatureTag.AVERAGE;
        else if (tmp >= COOL)
            return TemperatureTag.COOL;
        else if (tmp >= LITTLE_COLD + adjustment + 2 * weight)
            return TemperatureTag.LITTLE_COLD;
        else if (tmp >= COLD + adjustment + weight)
            return TemperatureTag.COLD;
        else
            return TemperatureTag.VERY_COLD;
    }
}
