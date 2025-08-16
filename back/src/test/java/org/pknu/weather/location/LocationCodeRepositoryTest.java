/*
package org.pknu.weather.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.pknu.weather.location.utils.AddressFinder;
import org.pknu.weather.location.dto.LocationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class LocationCodeRepositoryTest {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    AddressFinder addressFinder;

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

    @Test
    public void findProvinceList(){
        List<String> provinceList = addressFinder.getLocation();

        for (String province : provinceList) {
            log.info(province);
        }
    }

    @Test
    public void findCityList(){
        List<String> cityList = addressFinder.getLocation("경상남도");

        for (String city : cityList) {
            log.info(city);
        }
    }

    @Test
    public void findStreetList(){
        List<String> streetList = addressFinder.getLocation("경상남도", "창원시마산합포구");

        for (String street : streetList) {
            log.info(street);
        }
    }

}


*/
