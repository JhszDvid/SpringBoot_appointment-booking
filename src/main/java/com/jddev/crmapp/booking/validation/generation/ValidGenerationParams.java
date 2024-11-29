package com.jddev.crmapp.booking.validation.generation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidateGenerationRequest.class)
public @interface ValidGenerationParams {
    String message() default "Hibás paraméterek!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
