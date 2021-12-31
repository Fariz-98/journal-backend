package com.journal.journalbackend.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.journal.journalbackend.exceptions.InvalidAuthenticationHeaderException;
import com.journal.journalbackend.jwt.configuration.JwtConfig;
import com.journal.journalbackend.jwt.configuration.JwtSecretKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
public class JwtService {

    private final JwtConfig jwtConfig;
    private final JwtSecretKey jwtSecretKey;

    public JwtService(JwtConfig jwtConfig, JwtSecretKey jwtSecretKey) {
        this.jwtConfig = jwtConfig;
        this.jwtSecretKey = jwtSecretKey;
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader(jwtConfig.getAuthorizationHeader());

        if (authHeader == null || !authHeader.startsWith(jwtConfig.getTokenPrefix())) {
            throw new InvalidAuthenticationHeaderException("Missing refresh token");
        }

        try {
            String refreshToken = authHeader.replace(jwtConfig.getTokenPrefix(), "");
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey.getSecretKey())
                    .build()
                    .parseClaimsJws(refreshToken);

            Claims body = claimsJws.getBody();
            String username = body.getSubject();
            List<Map<String, String>> authorities = (List<Map<String, String>>) body.get("authorities");
            Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
            for (Map<String, String> tempMap: authorities) {
                grantedAuthorities.add(new SimpleGrantedAuthority(tempMap.get("authority")));
            }

            String accessToken = Jwts.builder()
                    .setSubject(username)
                    .claim("authorities", grantedAuthorities)
                    .setIssuedAt(Date.valueOf(LocalDate.now()))
                    .setExpiration(Date.valueOf(LocalDate.now().plusDays(jwtConfig.getAccessExpiredAfterDays())))
                    .signWith(jwtSecretKey.getSecretKey())
                    .compact();

            response.addHeader(jwtConfig.getAccessHeader(), jwtConfig.getTokenPrefix() + accessToken);
            response.addHeader(jwtConfig.getRefreshHeader(), jwtConfig.getTokenPrefix() + refreshToken);
        } catch (JwtException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(APPLICATION_JSON_VALUE);

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("httpStatus", HttpStatus.UNAUTHORIZED.name());
            errorResponse.put("dateTime", LocalDateTime.now().toString());
            new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);

            throw new JwtException(e.getMessage());
        }
    }

}
