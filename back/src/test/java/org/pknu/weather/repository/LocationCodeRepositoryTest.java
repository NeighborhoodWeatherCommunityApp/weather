package org.pknu.weather.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.pknu.weather.dto.LocationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;
/*
@SpringBootTest
@Slf4j
public class LocationCodeRepositoryTest {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Test
    public void getLocationCode() {

        LocationDTO locationDTO = LocationDTO.builder()
                .province("전라남도")
                .city("화순군")
                .street("도곡면")
                .build();

        String sql = "SELECT C1 FROM location_code WHERE C2 = :province AND C3 = :city AND C4 = :street";

        try {

            Map<String, Object> param = Map.of("province", locationDTO.getProvince(),"city",locationDTO.getCity(),"street",locationDTO.getStreet());

            Long locationCode = jdbcTemplate.queryForObject(sql, param, Long.class);

            System.out.println("Query result: " + locationCode);

        } catch (EmptyResultDataAccessException e) {
            System.out.println("No results found.");
        }

    }

    @Test
    public void getLocationCodeWith2Args() {

        LocationDTO locationDTO = LocationDTO.builder()
                .province("전라남도")
                .city("화순군")
                .street("도곡면")
                .build();

        String sql = "SELECT C1 FROM location_code WHERE C2 = :province AND C3 = :city AND C4 IS null";

        try {

            Map<String, Object> param = Map.of("province", locationDTO.getProvince(),"city",locationDTO.getCity());

            Long locationCode = jdbcTemplate.queryForObject(sql, param, Long.class);

            System.out.println("Query result: " + locationCode);

        } catch (EmptyResultDataAccessException e) {
            System.out.println("No results found.");
        }

    }
}
*/