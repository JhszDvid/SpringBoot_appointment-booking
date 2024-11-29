package com.jddev.crmapp.authentication.service;
import com.jddev.crmapp.authentication.dto.Token;
import com.jddev.crmapp.authentication.dto.request.LoginRequest;
import com.jddev.crmapp.authentication.dto.request.RegistrationRequest;
import com.jddev.crmapp.authentication.model.AppUser;
import jakarta.mail.MessagingException;


public interface IAuthenticationService {
    public AppUser RegisterUser(RegistrationRequest body);
    public Token LoginUser(LoginRequest body);
}
