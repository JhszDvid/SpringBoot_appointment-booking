package com.jddev.crmapp.booking.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.jddev.crmapp.booking.validation.generation.ValidGenerationParams;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
@ValidGenerationParams
public record GenerateAppointmentRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @FutureOrPresent
        LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @FutureOrPresent
        LocalDate endDate,
        HashSet<Integer> excludedDays,
        HashSet<LocalDate> excludedDates,

        @DateTimeFormat(iso = DateTimeFormat.ISO.TIME, pattern = "HH:mm")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,
        @DateTimeFormat(iso = DateTimeFormat.ISO.TIME, pattern = "HH:mm")
        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime,

        @DateTimeFormat(iso = DateTimeFormat.ISO.TIME, pattern = "HH:mm")
        @JsonFormat(pattern = "HH:mm")
        LocalTime breakStart,
        @DateTimeFormat(iso = DateTimeFormat.ISO.TIME, pattern = "HH:mm")
        @JsonFormat(pattern = "HH:mm")
        LocalTime breakEnd,
        @Min(1)
        @Max(32767)
        Short duration
) {
}
