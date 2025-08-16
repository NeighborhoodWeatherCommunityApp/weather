package org.pknu.weather.common.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.pknu.weather.common.validation.validator.PostExistValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PostExistValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface IsPositive {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
