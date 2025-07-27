package org.pknu.weather.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.pknu.weather.common.mapper.EnumTagMapper;
import org.pknu.weather.domain.tag.EnumTag;
import org.pknu.weather.weather.dto.TotalWeatherDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EnumTagTest {
    @Autowired
    EnumTagMapper enumTagMapper;

    @org.junit.jupiter.api.Test
    void enumTag_name_중복_검사_테스트() {
        // then
        assertThrows(IllegalArgumentException.class, () -> {
            enumTagMapper.put(Test.class);
        });
    }
}

enum Test implements EnumTag {
    COMMON;

    @Override
    public EnumTag findByCode(int code) {
        return null;
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public Integer getCode() {
        return null;
    }

    @Override
    public EnumTag weatherValueToTag(TotalWeatherDto totalWeatherDto) {
        return null;
    }
}
