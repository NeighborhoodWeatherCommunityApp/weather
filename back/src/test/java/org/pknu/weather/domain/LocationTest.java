package org.pknu.weather.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class LocationTest {

    @Test
    void LocationStreetNameReplaceTest() {
        // given
        String[] names = {
                "대연제1동", "대신동", "중계1동", "중계2,3동", "제원동", "공릉1동", "대산면", "부안읍", "종로1.2.3.4가동", "성수2가제3동", "남제동", "서제1동"
        };

        List<Location> locationList = new ArrayList<>();
        for(String name : names) {
            Location location = Location.builder()
                    .street(name)
                    .build();

            locationList.add(location);
        }

        // when

        // then

    }

}