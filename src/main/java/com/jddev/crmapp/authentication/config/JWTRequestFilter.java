package com.jddev.crmapp.authentication.config;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jddev.crmapp.authentication.service.AuthenticationService;
import com.jddev.crmapp.utility.token.ITokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private final String ACCESS_HEADER = "Authorization";
    private final String BEARER_PREFIX = "Bearer ";
    private final String REFRESH_HEADER = "X-Refresh-Token";
    private final Integer TOKEN_START_INDEX = 7;


    private final AuthenticationService authenticationService;
    private final ITokenService tokenService;
    private final UserDetailsService userDetailsService;

    public JWTRequestFilter(AuthenticationService authenticationService, ITokenService tokenService,
                            UserDetailsService userDetailsService) {
        this.authenticationService = authenticationService;
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(ACCESS_HEADER);
        String refreshHeader = request.getHeader(REFRESH_HEADER);
        if(authHeader == null || !authHeader.startsWith(BEARER_PREFIX) || refreshHeader == null || !refreshHeader.startsWith(BEARER_PREFIX))
        {
            filterChain.doFilter(request, response);
            return;
        }

        try{
            String token = authHeader.substring(TOKEN_START_INDEX);
            DecodedJWT accessToken = tokenService.validateToken(token);
            String username = tokenService.getClaim(accessToken, "USERNAME");

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, null);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (TokenExpiredException e)
        {
            String token = refreshHeader.substring(TOKEN_START_INDEX);
            try {
                DecodedJWT refreshToken = tokenService.validateToken(token);
                if(tokenService.getClaim(refreshToken, "TOKEN_TYPE").equals("REFRESH_TOKEN")) {
                    String newAccessToken = tokenService.generateAccessToken(tokenService.getClaim(refreshToken, "USERNAME"));
                    response.setHeader("ACCESS_TOKEN", newAccessToken);
                }
            }
            catch (Exception ex)
            {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request,response);
    }
}
