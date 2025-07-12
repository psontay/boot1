package com.boot1.validator;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DobValidator.class})
public @interface DobConstraint {
    String message() default "INVALID_DATE_OF_BIRTH";

    int min();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
