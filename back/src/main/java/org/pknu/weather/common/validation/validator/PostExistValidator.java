package org.pknu.weather.common.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.common.validation.annotation.IsPositive;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostExistValidator implements ConstraintValidator<IsPositive, Long> {
    @Override
    public void initialize(IsPositive constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        boolean isValid = value > 0;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus._MUST_BE_POSITIVE.toString())
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
