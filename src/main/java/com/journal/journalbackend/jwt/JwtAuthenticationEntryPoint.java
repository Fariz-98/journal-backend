package com.journal.journalbackend.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/*
 * Used to handle access denied exception for unauthenticated user
 */
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        Map<String, String> errResponse = new HashMap<>();
        errResponse.put("dateTime", LocalDateTime.now().toString());
        errResponse.put("httpStatus", HttpStatus.UNAUTHORIZED.name());
        errResponse.put("message", "Unauthenticated");
        errResponse.put("uri", request.getServletPath());
        new ObjectMapper().writeValue(response.getOutputStream(), errResponse);
    }

}
