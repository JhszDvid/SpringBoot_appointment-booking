package com.jddev.crmapp.utility.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jddev.crmapp.authentication.enums.TokenType;
import com.jddev.crmapp.authentication.model.AppUser;
import com.jddev.crmapp.utility.token.ITokenService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class TokenServiceImpl implements ITokenService {

    @Value("${encryption.jwt.private}")
    private String privateKey;

    @Value ("${encryption.jwt.issuer}")
    private String issuer;

    @Value ("${encryption.jwt.expiryInSecondsAccess}")
    private Integer expiryInSecondsAccess;

    @Value ("${encryption.jwt.expiryInSecondsRefresh}")
    private Integer expiryInSecondsRefresh;

    private Algorithm algorithm;

    @PostConstruct
    private void Start()
    {
        algorithm = Algorithm.HMAC512(privateKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateAccessToken(String username) {
        return JWT.create()
                .withClaim(USERNAME_KEY, username)
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000*expiryInSecondsAccess)))
                .withIssuer(issuer)
                .withClaim(TOKENTYPE_KEY, TokenType.ACCESS_TOKEN.toString())
                .sign(algorithm);
    }

    @Override
    public String generateRefreshToken(AppUser user) {
        return JWT.create()
                .withClaim(USERNAME_KEY, user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000*expiryInSecondsRefresh)))
                .withIssuer(issuer)
                .withClaim(TOKENTYPE_KEY, TokenType.REFRESH_TOKEN.toString())
                .sign(algorithm);
    }

    @Override
    public DecodedJWT validateToken(String token) {
        return JWT.require(algorithm).build().verify(token);
    }

    @Override
    public String getClaim(DecodedJWT token, String key) {
        return token.getClaim(key).asString();
    }
}
