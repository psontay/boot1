package com.boot1.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<UsernameConstraint , String> {
    @Override
    public boolean isValid( String username , ConstraintValidatorContext constraintValidatorContext) {
        if ( username == null || username.isBlank()) return true;
        return username.chars().anyMatch( Character::isUpperCase );
    }
    @Override
    public void initialize ( UsernameConstraint usernameConstraint ) {
        ConstraintValidator.super.initialize( usernameConstraint );
    }
}
