package com.boot1.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsernameValidator.class)
public @interface UsernameConstraint {
    String message() default "USERNAME_TYPE_INVALID";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
