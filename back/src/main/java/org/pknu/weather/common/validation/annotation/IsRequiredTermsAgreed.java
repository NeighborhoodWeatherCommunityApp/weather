package org.pknu.weather.common.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.pknu.weather.common.validation.validator.IsRequiredTermsAgreedValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IsRequiredTermsAgreedValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface IsRequiredTermsAgreed {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
