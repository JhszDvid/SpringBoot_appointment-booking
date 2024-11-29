package com.jddev.crmapp.authentication.dto;

public record Token(
        String accessToken,
        String refreshToken
) {
}
