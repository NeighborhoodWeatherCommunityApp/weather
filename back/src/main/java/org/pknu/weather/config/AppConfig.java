package org.pknu.weather.config;

import org.pknu.weather.tag.enums.EnumTagMapper;
import org.pknu.weather.tag.enums.DustTag;
import org.pknu.weather.tag.enums.HumidityTag;
import org.pknu.weather.tag.enums.SkyTag;
import org.pknu.weather.tag.enums.TemperatureTag;
import org.pknu.weather.tag.enums.WindTag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public EnumTagMapper enumTagMapper() {
        EnumTagMapper enumTagMapper = new EnumTagMapper();

        enumTagMapper.put(TemperatureTag.class);
        enumTagMapper.put(WindTag.class);
        enumTagMapper.put(HumidityTag.class);
        enumTagMapper.put(SkyTag.class);
        enumTagMapper.put(DustTag.class);

        return enumTagMapper;
    }
}
