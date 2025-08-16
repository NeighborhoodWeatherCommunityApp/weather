package org.pknu.weather.common.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.pknu.weather.common.validation.validator.PostFieldsRequiredValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PostFieldsRequiredValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface PostFieldsRequired {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
