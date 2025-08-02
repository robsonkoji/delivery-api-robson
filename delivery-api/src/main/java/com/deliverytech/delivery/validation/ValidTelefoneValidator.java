package com.deliverytech.delivery.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidTelefoneValidator implements ConstraintValidator<ValidTelefone, String> {

    private static final String TELEFONE_REGEX = "^\\d{10,11}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(TELEFONE_REGEX);
    }
}
