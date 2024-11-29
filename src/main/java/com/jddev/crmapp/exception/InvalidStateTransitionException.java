package com.jddev.crmapp.exception;

import org.springframework.validation.BindingResult;

public class InvalidStateTransitionException extends RuntimeException {

    private final BindingResult bindingResult;

    public InvalidStateTransitionException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }
}
