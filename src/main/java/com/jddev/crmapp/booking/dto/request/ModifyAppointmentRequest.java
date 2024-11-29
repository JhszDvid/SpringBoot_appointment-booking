package com.jddev.crmapp.booking.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jddev.crmapp.booking.enums.AppointmentStatus;
import com.jddev.crmapp.booking.validation.date.OptionalFutureOrPresent;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public record ModifyAppointmentRequest(

        AppointmentStatus status,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @FutureOrPresent
        LocalDate startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.TIME, pattern = "HH:mm")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,
        @Min(1)
        @Max(32767)
        Short durationInMinutes,
        @Size(max=255)
        String description,
        @Min(0)
        @Max(999999999)
        Float price,
        @Email
        String bookedByEmail

) {
}
