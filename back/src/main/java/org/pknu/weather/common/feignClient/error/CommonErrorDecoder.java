package org.pknu.weather.common.feignClient.error;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class CommonErrorDecoder implements ErrorDecoder {

    private final Map<String, ErrorDecoder> errorDecoderMap;
    private final ErrorDecoder defaultErrorDecoder = new Default();

    public CommonErrorDecoder(Map<String, ErrorDecoder> errorDecoderMap) {
        this.errorDecoderMap = errorDecoderMap;
    }


    @Override
    public Exception decode(final String methodKey, final Response response) {
        String className = methodKey.split("#")[0];

        if(errorDecoderMap.containsKey(className)) {
            return errorDecoderMap.get(className).decode(methodKey, response);
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }
}
