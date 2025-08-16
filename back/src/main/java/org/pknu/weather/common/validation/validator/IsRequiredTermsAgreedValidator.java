package org.pknu.weather.common.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.common.validation.annotation.IsRequiredTermsAgreed;
import org.pknu.weather.member.dto.TermsDto;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IsRequiredTermsAgreedValidator implements ConstraintValidator<IsRequiredTermsAgreed, TermsDto> {
    @Override
    public void initialize(IsRequiredTermsAgreed constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(TermsDto parameters, ConstraintValidatorContext context) {
        if(parameters == null) {
            return false;
        }

        if (isRequiredTermsAgreed(parameters)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus._ESSENTIAL_TERMS_IS_REQUIRED.toString())
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean isRequiredTermsAgreed(TermsDto termsDto) {
        return termsDto.getIsPrivacyPolicyAgreed() &&
                termsDto.getIsLocationServiceTermsAgreed() &&
                termsDto.getIsServiceTermsAgreed();
    }
}