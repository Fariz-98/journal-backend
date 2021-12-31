package com.journal.journalbackend.jwt.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
@ConfigurationProperties(prefix = "application.jwt")
@Getter
@Setter
public class JwtConfig {

    private String secretKey;
    private String tokenPrefix;
    private String accessHeader;
    private String refreshHeader;
    private Integer accessExpiredAfterDays;
    private Integer refreshExpiredAfterDays;

    public String getAuthorizationHeader() {
        return HttpHeaders.AUTHORIZATION;
    }

}
