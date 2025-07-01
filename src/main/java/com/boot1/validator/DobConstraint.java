package com.boot1.validator;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint( validatedBy = { DobValidator.class} )
public @interface DobConstraint {
    String message() default "INVALID_DATE_OF_BIRTH";
    int min();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
