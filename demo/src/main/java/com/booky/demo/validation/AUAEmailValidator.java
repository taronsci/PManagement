package com.booky.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AUAEmailValidator implements ConstraintValidator<AUAEmail, String> {

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if(email == null)
            return false;

        String regex = "^[A-Za-z0-9._%+-]+@edu\\.aua\\.am$";

        return email.matches(regex);
    }
}
