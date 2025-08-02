package com.deliverytech.delivery.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidHorarioFuncionamentoValidator implements ConstraintValidator<ValidHorarioFuncionamento, String> {

    private static final String HORARIO_REGEX = "^([01]\\d|2[0-3]):[0-5]\\d-([01]\\d|2[0-3]):[0-5]\\d$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(HORARIO_REGEX);
    }
}
