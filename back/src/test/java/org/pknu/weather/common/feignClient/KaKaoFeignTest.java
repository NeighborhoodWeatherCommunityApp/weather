/*
package org.pknu.weather.common.feignClient;

import feign.Response;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.pknu.weather.member.auth.feignClient.KaKaoAuthClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class KaKaoFeignTest {

    @Autowired
    KaKaoAuthClient kaKaoLoginClient;

    @Test
    public void getSigsAccessToken() throws IOException {
        Response result = kaKaoLoginClient.deleteMemberData("KakaoAdminKey", "user_id",3644709642L);
        System.out.println(result.status());
    }
}
*/
