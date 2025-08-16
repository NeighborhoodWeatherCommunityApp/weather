package org.pknu.weather.location.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Component
public class AddressFinder {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<String> getLocation(){

        String sql = "SELECT distinct C2 FROM location_code";

        try {

            return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("C2"));

        } catch (EmptyResultDataAccessException e) {
            log.warn("도(광역시)결과가 비어있습니다.");
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    public List<String> getLocation(String province){

        String sql = "SELECT distinct C3 FROM location_code WHERE C2 = :province AND C3 IS NOT NULL";

        try {

            Map<String, Object> param = Map.of("province", province);
            return jdbcTemplate.queryForList(sql, param, String.class);

        } catch (EmptyResultDataAccessException e) {
            log.warn("시군구 결과가 비어있습니다.");
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    public List<String> getLocation(String province, String city){

        String sql = "SELECT distinct C4 FROM location_code WHERE C2 = :province AND C3 = :city AND C4 IS NOT NULL";

        try {

            Map<String, Object> param = Map.of("province", province, "city",city);
            return jdbcTemplate.queryForList(sql, param, String.class);

        } catch (EmptyResultDataAccessException e) {
            log.warn("읍면동 결과가 비어있습니다.");
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }
}
