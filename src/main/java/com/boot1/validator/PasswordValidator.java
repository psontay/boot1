package com.boot1.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordConstraint, String> {
    @Override
    public boolean isValid (String password , ConstraintValidatorContext context) {
        if ( password == null || password.isBlank() ) return true;
        return password.chars().anyMatch(Character::isUpperCase);
    }
    @Override
    public void initialize(PasswordConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
