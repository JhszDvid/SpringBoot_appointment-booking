package com.jddev.crmapp.utility.token;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.jddev.crmapp.authentication.model.AppUser;

public interface ITokenService {
    public final String USERNAME_KEY = "USERNAME";
    public final String TOKENTYPE_KEY = "TOKEN_TYPE";

    public String generateAccessToken(String username);
    public String generateRefreshToken(AppUser user);
    public DecodedJWT validateToken(String token);
    public String getClaim(DecodedJWT token, String key);
}
