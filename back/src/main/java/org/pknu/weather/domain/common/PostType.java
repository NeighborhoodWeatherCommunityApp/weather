package org.pknu.weather.domain.common;

import org.pknu.weather.apiPayload.code.status.ErrorStatus;
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
