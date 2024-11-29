package com.jddev.crmapp.booking.validation.generation;

import com.jddev.crmapp.booking.dto.request.GenerateAppointmentRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class ValidateGenerationRequest implements ConstraintValidator<ValidGenerationParams, GenerateAppointmentRequest> {
    @Override
    public boolean isValid(GenerateAppointmentRequest request, ConstraintValidatorContext context) {
        if(request.startDate() == null || request.startTime() == null || request.endDate() == null || request.endTime() == null || request.duration() == null){
            context.buildConstraintViolationWithTemplate("hiányzik!")
                    .addPropertyNode("Kötelező mező")
                    .addConstraintViolation();
            return false;
        }

        LocalDate startDate = request.startDate();
        LocalDate endDate = request.endDate();

        if(startDate.isAfter(endDate) || endDate.isBefore(startDate)) {
            context.buildConstraintViolationWithTemplate("nem lehet a végdátum után!")
                    .addPropertyNode("Kezdődátum")
                    .addConstraintViolation();
            return false;
        }

        LocalTime startTime = request.startTime();
        LocalTime endTime = request.endTime();
        Short duration = request.duration();
        if(startTime.plusMinutes(duration).isAfter(endTime)) {
            context.buildConstraintViolationWithTemplate("Nincs elég idő egy időpont létrehozására sem!")
                    .addPropertyNode("Kezdő időpont")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
