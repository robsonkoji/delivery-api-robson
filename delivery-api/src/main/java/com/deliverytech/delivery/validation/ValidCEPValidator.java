package com.deliverytech.delivery.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCEPValidator implements ConstraintValidator<ValidCEP, String> {

    private static final String CEP_REGEX = "^\\d{5}-\\d{3}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(CEP_REGEX);
    }
}
