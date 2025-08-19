package com.renewsim.backend.auth_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "auth.rate-limiting")
public class SecurityRateLimitProperties {
    public enum Strategy { IP, IP_USER }

    private boolean enabled = true;
    private Strategy strategy = Strategy.IP;
    private int maxAttempts = 5;
    private int windowSeconds = 60;
    private int retryAfterSeconds = 60;
    private String loginPath = "/api/v1/auth/login";
}
