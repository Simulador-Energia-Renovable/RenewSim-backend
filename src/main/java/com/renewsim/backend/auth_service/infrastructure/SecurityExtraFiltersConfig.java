package com.renewsim.backend.auth_service.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renewsim.backend.auth_service.config.SecurityRateLimitProperties;
import com.renewsim.backend.auth_service.infrastructure.security.LoginRateLimitingFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableConfigurationProperties(SecurityRateLimitProperties.class)
public class SecurityExtraFiltersConfig {

    @Bean
    @ConditionalOnProperty(prefix = "auth.rate-limiting", name = "enabled", havingValue = "true", matchIfMissing = true)
    public LoginRateLimitingFilter loginRateLimitingFilter(SecurityRateLimitProperties props, ObjectMapper objectMapper) {
        return new LoginRateLimitingFilter(props, objectMapper);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, LoginRateLimitingFilter rateFilter) throws Exception {
        http
          .csrf(csrf -> csrf.disable())
          .authorizeHttpRequests(auth -> auth
              .requestMatchers("/api/v1/auth/**").permitAll()
              .anyRequest().authenticated()
          )
          .addFilterBefore(rateFilter, UsernamePasswordAuthenticationFilter.class)
          .httpBasic(withDefaults());
        return http.build();
    }
}

