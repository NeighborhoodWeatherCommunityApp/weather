package org.pknu.weather.common;

import org.pknu.weather.domain.common.RainRange;
import org.pknu.weather.domain.common.Sensitivity;
import org.pknu.weather.domain.common.TemperatureRange;

public class WeatherRangeConverter {
    private static final Integer VERY_HOT = 33;
    private static final Integer HOT = 28;
    private static final Integer LITTLE_HOT = 23;
    private static final Integer AVERAGE = 20;
    private static final Integer COOL = 17;
    private static final Integer LITTLE_COLD = 12;
    private static final Integer COLD = 5;

    public static RainRange rain2Text(Float rain) {
        if(rain == 0.0) return RainRange.NOTHING;
        else if(rain <= 1.0) return RainRange.ALMOST_NOTHING;
        else if(rain <= 3.0) return RainRange.VERY_WEAK;
        else if(rain <= 6.0) return RainRange.WEAK;
        else if(rain <= 10.0) return RainRange.AVERAGE;
        else if( rain <= 20.0) return RainRange.STRONG;
        else return RainRange.VERY_STRONG;
    }

    public static TemperatureRange tmp2Text(Integer tmp, Sensitivity sensitivity) {
        if(sensitivity == Sensitivity.HOT) {
            if(tmp >= VERY_HOT - 3) return TemperatureRange.VERY_HOT;
            else if(tmp >= HOT - 2) return TemperatureRange.HOT;
            else if(tmp >= LITTLE_HOT - 1) return TemperatureRange.LITTLE_HOT;
            else if(tmp >= AVERAGE) return TemperatureRange.AVERAGE;
            else if(tmp >= COOL) return TemperatureRange.COOL;
            else if(tmp >= LITTLE_COLD - 1) return TemperatureRange.LITTLE_COLD;
            else if(tmp >= COLD - 2) return TemperatureRange.COLD;
            else return TemperatureRange.VERY_COLD;
        } else if(sensitivity == Sensitivity.COLD) {
            if(tmp >= VERY_HOT + 3) return TemperatureRange.VERY_HOT;
            else if(tmp >= HOT + 2) return TemperatureRange.HOT;
            else if(tmp >= LITTLE_HOT + 1) return TemperatureRange.LITTLE_HOT;
            else if(tmp >= AVERAGE) return TemperatureRange.AVERAGE;
            else if(tmp >= COOL) return TemperatureRange.COOL;
            else if(tmp >= LITTLE_COLD + 1) return TemperatureRange.LITTLE_COLD;
            else if(tmp >= COLD + 2) return TemperatureRange.COLD;
            else return TemperatureRange.VERY_COLD;
        } else {
            if(tmp >= VERY_HOT) return TemperatureRange.VERY_HOT;
            else if(tmp >= HOT) return TemperatureRange.HOT;
            else if(tmp >= LITTLE_HOT) return TemperatureRange.LITTLE_HOT;
            else if(tmp >= AVERAGE) return TemperatureRange.AVERAGE;
            else if(tmp >= COOL) return TemperatureRange.COOL;
            else if(tmp >= LITTLE_COLD) return TemperatureRange.LITTLE_COLD;
            else if(tmp >= COLD) return TemperatureRange.COLD;
            else return TemperatureRange.VERY_COLD;
        }
    }
}
