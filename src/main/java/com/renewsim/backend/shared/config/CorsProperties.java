package com.renewsim.backend.shared.config;

import lombok.Getter; import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList; import java.util.List;

@Getter @Setter
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {
    private boolean enabled = true;
    private List<String> allowedOrigins = new ArrayList<>();
    private List<String> allowedMethods = List.of("GET","POST","OPTIONS");
    private List<String> allowedHeaders = List.of("Content-Type","Authorization");
    private List<String> exposedHeaders = List.of("X-Correlation-Id");
    private boolean allowCredentials = true;
    private long maxAgeSeconds = 3600;
}

