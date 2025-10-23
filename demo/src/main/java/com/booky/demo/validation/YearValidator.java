package com.booky.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class YearValidator implements ConstraintValidator<MaxCurrentYear,Integer> {
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if(value == null)
            return true;

        int currentYear = Year.now().getValue();
        return value <= currentYear;
    }
}
