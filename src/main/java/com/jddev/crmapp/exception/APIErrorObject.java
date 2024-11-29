package com.jddev.crmapp.exception;

import org.springframework.http.HttpStatus;

import java.util.Date;

public class APIErrorObject {
    private final HttpStatus status;
    private final String message;
    private final Date timestamp;

    public APIErrorObject(HttpStatus statusCode, String message, Date timestamp) {
        this.status = statusCode;
        this.message = message;
        this.timestamp = timestamp;
    }

    public HttpStatus getStatusCode() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}

