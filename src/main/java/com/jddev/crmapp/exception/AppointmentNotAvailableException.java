package com.jddev.crmapp.exception;

public class AppointmentNotAvailableException extends RuntimeException{
    public AppointmentNotAvailableException(String message){
        super(message);
    }
}