package org.pknu.weather.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.dto.PostRequest;
import org.pknu.weather.validation.annotation.PostFieldsRequired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostFieldsRequiredValidator implements ConstraintValidator<PostFieldsRequired, PostRequest.CreatePostAndTagParameters> {
    @Override
    public void initialize(PostFieldsRequired constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(PostRequest.CreatePostAndTagParameters parameters, ConstraintValidatorContext context) {
        if (parameters.parametersIsEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus._POST_CONTENT_OR_TAGS_REQUIRED.toString())
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}