package org.pknu.weather.common;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TokenConverter {

    private static final String key;

    static {
        key = System.getenv("JWT_KEY");
    }

    public static String getEmailByToken(String auth){

        String accessToken = auth.substring(7);

        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));

        Map<String, Object> claim = null;

        claim = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

        return String.valueOf(claim.get("email"));
    }
}