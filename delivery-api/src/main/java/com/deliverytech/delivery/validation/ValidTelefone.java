package com.deliverytech.delivery.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidTelefoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTelefone {
    String message() default "Telefone inválido. Deve conter 10 ou 11 dígitos numéricos.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
