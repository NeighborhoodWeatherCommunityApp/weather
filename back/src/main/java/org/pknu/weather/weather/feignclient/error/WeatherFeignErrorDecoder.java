package org.pknu.weather.weather.feignclient.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.security.exception.WeatherFeignClientException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

@Slf4j
@Component
public class WeatherFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(final String methodKey, final Response response) {
        try {
            // Content-Type 확인
            String contentType = getContentType(response);

            if (response.body() != null) {
                String body = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);

                if (contentType.contains("application/json")) {
                    // JSON 응답 처리
                    return handleJsonResponse(body);
                } else if (contentType.contains("text/xml") || contentType.contains("application/xml")) {
                    // XML 응답 처리
                    return handleXmlResponse(body);
                }
            }
        } catch (Exception e) {
            log.error("WeatherFeignClient - Weather API 응답 파싱 중 오류 발생", e);
            return new WeatherFeignClientException(ErrorStatus._API_SERVER_ERROR);
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }

    /**
     * JSON 응답을 처리하는 메서드
     */
    private Exception handleJsonResponse(String body) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(body);

        String resultCode = jsonNode.path("response").path("header").path("resultCode").asText();
        String resultMsg = jsonNode.path("response").path("header").path("resultMsg").asText();

        log.error("WeatherFeignClient JSON 응답 오류 - resultCode: {}, resultMsg: {}", resultCode, resultMsg);
        return new WeatherFeignClientException(ErrorStatus._API_SERVER_ERROR, resultCode, resultMsg);
    }

    /**
     * XML 응답을 처리하는 메서드
     */
    private Exception handleXmlResponse(String body) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));

        NodeList resultCodeNode = document.getElementsByTagName("resultCode");
        NodeList resultMsgNode = document.getElementsByTagName("resultMsg");

        String resultCode = (resultCodeNode.getLength() > 0) ? resultCodeNode.item(0).getTextContent() : "xx";
        String resultMsg = (resultMsgNode.getLength() > 0) ? resultMsgNode.item(0).getTextContent() : "알 수 없는 에러";

        log.error("WeatherFeignClient XML 응답 오류 - resultCode: {}, resultMsg: {}", resultCode, resultMsg);
        return new WeatherFeignClientException(ErrorStatus._API_SERVER_ERROR, resultCode, resultMsg);
    }

    /**
     * Response 헤더에서 Content-Type 값을 가져오는 메서드
     */
    private String getContentType(Response response) {
        for (Map.Entry<String, Collection<String>> entry : response.headers().entrySet()) {
            if ("Content-Type".equalsIgnoreCase(entry.getKey())) {
                return entry.getValue().iterator().next();
            }
        }
        return "unknown";
    }
}
