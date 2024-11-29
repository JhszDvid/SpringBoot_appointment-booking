package com.jddev.crmapp.authentication.dto.response;

import com.jddev.crmapp.authentication.dto.Token;

public record LoginResponse(
        Token token
) {
}
