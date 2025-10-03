package org.pknu.weather.weather.feignclient.weatherapi.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
public class KmsApiHubParamDTO {
    private String authKey;

    private String dataType;

    private Integer pageNo;

    private Integer numOfRows;

    private String base_date;

    private String base_time;

    private Integer nx;

    private Integer ny;

    @Builder
    private KmsApiHubParamDTO(String authKey, Integer pageNo, Integer numOfRows, String base_date, String base_time,
                              Integer nx, Integer ny) {
        this.authKey = authKey;
        this.dataType = "JSON";
        this.pageNo = pageNo;
        this.numOfRows = numOfRows;
        this.base_date = base_date;
        this.base_time = base_time;
        this.nx = nx;
        this.ny = ny;
    }
}
