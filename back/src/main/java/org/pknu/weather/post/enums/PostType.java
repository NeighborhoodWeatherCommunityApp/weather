package org.pknu.weather.post.enums;

import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;

public enum PostType {
    WEATHER, RUN, HIKING, PET;

    public static PostType toPostType(String string) {
        try {
            return valueOf(string);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._POST_TYPE_NOT_FOUND);
        }
    }
}
