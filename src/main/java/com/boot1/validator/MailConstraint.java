package com.boot1.validator;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint( validatedBy = { MailValidator.class})
public @interface MailConstraint {
    String message() default "INVALID_EMAIL_TYPE";
    String domain();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
