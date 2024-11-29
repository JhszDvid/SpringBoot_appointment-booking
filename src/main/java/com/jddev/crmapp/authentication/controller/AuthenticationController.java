package com.jddev.crmapp.authentication.controller;

import com.jddev.crmapp.authentication.dto.Token;
import com.jddev.crmapp.authentication.dto.request.LoginRequest;
import com.jddev.crmapp.authentication.dto.request.RegistrationRequest;
import com.jddev.crmapp.authentication.dto.response.LoginResponse;
import com.jddev.crmapp.authentication.dto.response.RegistrationResponse;
import com.jddev.crmapp.authentication.model.AppUser;
import com.jddev.crmapp.authentication.service.AuthenticationService;
import com.jddev.crmapp.exception.APIResponseObject;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@Validated
public class AuthenticationController {

    private final AuthenticationService AuthenticationService;

    public AuthenticationController(AuthenticationService AuthenticationService) {
        this.AuthenticationService = AuthenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest body) throws MessagingException {

        AppUser newUser = AuthenticationService.RegisterUser(body);

        return new APIResponseObject.Builder()
                .withMessage("User succesfully created!")
                .withStatusCode(HttpStatus.CREATED)
                .withObject(new RegistrationResponse(newUser.getEmail()))
                .buildResponse();
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest body)
    {
        Token token = AuthenticationService.LoginUser(body);

        return new APIResponseObject.Builder()
                .withObject(new LoginResponse(token))
                .buildResponse();
    }
}
