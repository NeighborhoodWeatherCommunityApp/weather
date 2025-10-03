package org.pknu.weather.location.feignClient.dto;

public class LocationRequestDTO {

    private String accessToken;
    private double x_coor;
    private double y_coor;
    private int addr_type;
}
