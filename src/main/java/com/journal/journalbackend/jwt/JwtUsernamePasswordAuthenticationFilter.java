package com.journal.journalbackend.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.journal.journalbackend.jwt.configuration.JwtConfig;
import com.journal.journalbackend.jwt.configuration.JwtSecretKey;
import com.journal.journalbackend.jwt.payload.UsernameAndPasswordAuthenticationRequest;
import io.jsonwebtoken.Jwts;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final JwtSecretKey jwtSecretKey;

    @Autowired
    public JwtUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager, JwtConfig jwtConfig, JwtSecretKey jwtSecretKey) {
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.jwtSecretKey = jwtSecretKey;

        setFilterProcessesUrl("/api/login");
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UsernameAndPasswordAuthenticationRequest usernameAndPasswordAuthenticationRequest =
                    new ObjectMapper().readValue(request.getInputStream(), UsernameAndPasswordAuthenticationRequest.class);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    usernameAndPasswordAuthenticationRequest.getUsername(),
                    usernameAndPasswordAuthenticationRequest.getPassword()
            );

            return authenticationManager.authenticate(authentication);
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(APPLICATION_JSON_VALUE);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("httpStatus", HttpStatus.UNAUTHORIZED.name());
            errorResponse.put("dateTime", LocalDateTime.now().toString());
            new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
            throw new Exception(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String accessToken = Jwts.builder()
                .setSubject(authResult.getName())
                .claim("authorities", authResult.getAuthorities())
                .setIssuedAt(Date.valueOf(LocalDate.now()))
                .setExpiration(Date.valueOf(LocalDate.now().plusDays(jwtConfig.getAccessExpiredAfterDays())))
                .signWith(jwtSecretKey.getSecretKey())
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(authResult.getName())
                .claim("authorities", authResult.getAuthorities())
                .setIssuedAt(Date.valueOf(LocalDate.now()))
                .setExpiration(Date.valueOf(LocalDate.now().plusDays(jwtConfig.getRefreshExpiredAfterDays())))
                .signWith(jwtSecretKey.getSecretKey())
                .compact();

        response.setStatus(HttpStatus.NO_CONTENT.value());
        response.addHeader(jwtConfig.getAccessHeader(), jwtConfig.getTokenPrefix() + accessToken);
        response.addHeader(jwtConfig.getRefreshHeader(), jwtConfig.getTokenPrefix() + refreshToken);
    }

}































