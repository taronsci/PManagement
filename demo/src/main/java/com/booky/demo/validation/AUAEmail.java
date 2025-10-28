package com.booky.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AUAEmailValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AUAEmail {
    String message() default "You must have aua email to use our website";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
