package com.jddev.crmapp.feedback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFeedbackRequest(
        @Size(min=3, max=2000)
        @NotBlank
        String description
) {
}
