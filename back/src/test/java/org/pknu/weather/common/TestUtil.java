package org.pknu.weather.common;

import org.pknu.weather.member.entity.Member;
import org.pknu.weather.security.util.JWTUtil;

import java.lang.reflect.Field;
import java.util.Map;

public class TestUtil {

    /**
     * setter가 없어서 테스트를 못하기 때문에 만든 메서두
     *
     * @param target 값을 변경할 대상
     * @param fieldName 값을 변경할 필드 이름
     * @param value 변경할 값
     */
    public static void entitySetFiled(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getSuperclass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set field value", e);
        }
    }

    public static String generateJwtToken(JWTUtil jwtUtil, Member member) {
        // JWT 토큰을 생성하는 메서드
        Map<String, Object> claims = Map.of("id", member.getId(),"email", member.getEmail());
        return "Bearer " + jwtUtil.generateToken(claims ,1);
    }
}
