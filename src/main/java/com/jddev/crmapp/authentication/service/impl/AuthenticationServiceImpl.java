package com.jddev.crmapp.authentication.service.impl;

import com.jddev.crmapp.authentication.dto.Token;
import com.jddev.crmapp.authentication.dto.request.LoginRequest;
import com.jddev.crmapp.authentication.dto.request.RegistrationRequest;
import com.jddev.crmapp.authentication.model.AppUser;
import com.jddev.crmapp.authentication.repository.UserRepository;
import com.jddev.crmapp.authentication.service.IAuthenticationService;
import com.jddev.crmapp.utility.db.DbService;
import com.jddev.crmapp.utility.token.ITokenService;
import com.jddev.crmapp.exception.UserAlreadyExistsException;
import com.jddev.crmapp.exception.UserNotFoundException;
import com.jddev.crmapp.utility.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthenticationServiceImpl implements IAuthenticationService {

    Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private final DbService dbService;
    private final PasswordEncoder passwordEncoder;
    private final ITokenService ITokenService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationServiceImpl(DbService dbService, PasswordEncoder passwordEncoder, ITokenService ITokenService, EmailService emailService, AuthenticationManager authenticationManager) {
        this.dbService = dbService;
        this.passwordEncoder = passwordEncoder;
        this.ITokenService = ITokenService;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AppUser RegisterUser(RegistrationRequest body) {
        logger.info("started");
        boolean userExists = dbService.executeCustomMethod(UserRepository.class, repo -> ((UserRepository) repo).findByEmail(body.email())).isPresent();
        if(userExists)
            throw new UserAlreadyExistsException("A user with this email has already been created");

        AppUser newUser = new AppUser.Builder()
                .withUsername(body.email())
                .withPassword(passwordEncoder.encode(body.password()))
                .withLastLogin(LocalDateTime.now())
                .withVerified(true) // maybe change later to have a verification email sent :)
                .build();

        dbService.save(UserRepository.class, newUser);

        return newUser;
    }

    @Override
    public Token LoginUser(LoginRequest body) {
        AppUser user = dbService.executeCustomMethod(UserRepository.class, repo -> ((UserRepository) repo).findByEmail(body.email()))
                .orElseThrow(() -> new UserNotFoundException("Nem található felhasználó ezzel az email címmel!"));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(body.email(), body.password()));

        String accessToken = ITokenService.generateAccessToken(user.getEmail());
        String refreshToken = ITokenService.generateRefreshToken(user);


        return new Token(accessToken, refreshToken);
    }
}
