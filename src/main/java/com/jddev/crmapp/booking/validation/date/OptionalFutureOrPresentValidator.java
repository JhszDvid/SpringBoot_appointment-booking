package com.jddev.crmapp.booking.validation.date;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class OptionalFutureOrPresentValidator implements ConstraintValidator<OptionalFutureOrPresent, LocalDate> {
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Skip validation if the value is null
        }
        return !value.isBefore(LocalDate.now()); // Apply FutureOrPresent validation
    }
}
