package com.jddev.crmapp.booking.validation.date;

import com.jddev.crmapp.booking.validation.date.OptionalFutureOrPresentValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OptionalFutureOrPresentValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface OptionalFutureOrPresent {

    String message() default "must be a date in the present or in the future";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}