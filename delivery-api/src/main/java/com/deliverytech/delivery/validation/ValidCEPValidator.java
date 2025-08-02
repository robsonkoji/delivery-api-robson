package com.deliverytech.delivery.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCEPValidator implements ConstraintValidator<ValidCEP, String> {

    private static final Pattern CEP_REGEX = Pattern.compile("\\b\\d{5}-\\d{3}\\b");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        Matcher matcher = CEP_REGEX.matcher(value);
        return matcher.find();
    }
}
