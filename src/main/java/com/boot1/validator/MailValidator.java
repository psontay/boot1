package com.boot1.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class MailValidator implements ConstraintValidator<MailConstraint, String> {
    private String domain;
    @Override
    public boolean isValid (String userMail , ConstraintValidatorContext context) {
        if (Objects.isNull(userMail)) return false;
        return userMail.startsWith(this.domain) && userMail.endsWith("@gmail.com");
    }
    @Override
    public void initialize(MailConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.domain = constraintAnnotation.domain();
    }
}
