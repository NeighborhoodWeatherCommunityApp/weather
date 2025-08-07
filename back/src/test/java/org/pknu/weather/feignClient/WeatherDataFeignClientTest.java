package org.pknu.weather.feignClient;

import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.pknu.weather.feignClient.error.CommonErrorDecoder;
import org.pknu.weather.feignClient.error.WeatherFeignErrorDecoder;
import org.pknu.weather.security.exception.WeatherFeignClientException;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class WeatherDataFeignClientTest {
    private final CommonErrorDecoder errorDecoder = new CommonErrorDecoder(Map.of(WeatherFeignClient.class.getSimpleName(), new WeatherFeignErrorDecoder()));

    @Test
    void 실패시_JSON_응답이_정상적으로_처리되는지_확인하는_테스트() {
        // given
        String jsonErrorResponse = "{\n" +
                "  \"response\": {\n" +
                "    \"header\": {\n" +
                "      \"resultCode\": \"03\",\n" +
                "      \"resultMsg\": \"NO_DATA\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        Response response = Response.builder()
                .status(200)
                .reason("Success")
                .headers(Collections.singletonMap("Content-Type", Collections.singletonList("application/json")))
                .request(Request.create(Request.HttpMethod.GET, "/api/weather", Collections.emptyMap(), null, StandardCharsets.UTF_8))
                .body(jsonErrorResponse, StandardCharsets.UTF_8)  // JSON 바디 설정
                .build();

        // when
        Exception exception = errorDecoder.decode("WeatherFeignClient#getWeatherData", response);

        // then
        assertInstanceOf(WeatherFeignClientException.class, exception);
        assertEquals("03", ((WeatherFeignClientException) exception).getResultCode());
        assertEquals("NO_DATA", ((WeatherFeignClientException) exception).getResultMsg());
    }

    @Test
    void 실패시_XML_응답이_정상적으로_처리되는지_확인하는_테스트() {
        // given
        String xmlErrorResponse =
                "<response>\n" +
                        "   <header>\n" +
                        "       <resultCode>10</resultCode>\n" +
                        "       <resultMsg>파라미터가 잘못되엇습니다.</resultMsg>\n" +
                        "   </header>\n" +
                        "</response>";

        Response response = Response.builder()
                .status(200)
                .reason("Success")
                .headers(Collections.singletonMap("Content-Type", Collections.singletonList("text/xml")))
                .request(Request.create(Request.HttpMethod.GET, "/api/weather", Collections.emptyMap(), null, StandardCharsets.UTF_8))
                .body(xmlErrorResponse, StandardCharsets.UTF_8)
                .build();

        // when
        Exception exception = errorDecoder.decode("WeatherFeignClient#getWeatherData", response);

        // then
        assertInstanceOf(WeatherFeignClientException.class, exception);
        assertEquals("10", ((WeatherFeignClientException) exception).getResultCode());
        assertEquals("파라미터가 잘못되엇습니다.", ((WeatherFeignClientException) exception).getResultMsg());
    }
}