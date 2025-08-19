package com.renewsim.backend.auth_service.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.renewsim.backend.auth_service.application.port.in.AuthUseCase;
import com.renewsim.backend.auth_service.web.dto.AuthRequestDTO;
import com.renewsim.backend.auth_service.web.dto.AuthResponseDTO;
import com.renewsim.backend.shared.observability.AuthAuditLogger;
import com.renewsim.backend.shared.web.ClientIpExtractor;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = "application/json")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request,
                                                 HttpServletRequest httpReq) {
        try {
            AuthResponseDTO body = authUseCase.login(request);
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore().mustRevalidate().cachePrivate().noTransform())
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .body(body);
        } catch (Exception ex) {
            AuthAuditLogger.warnAuthFailure(
                    classifyAuthFailure(ex),
                    ClientIpExtractor.clientIp(httpReq),
                    request != null ? request.getUsername() : null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .cacheControl(CacheControl.noStore().mustRevalidate().cachePrivate().noTransform())
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .build();
        }
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody AuthRequestDTO request,
                                                    HttpServletRequest httpReq) {
        try {
            AuthResponseDTO body = authUseCase.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .cacheControl(CacheControl.noStore().mustRevalidate().cachePrivate().noTransform())
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .body(body);
        } catch (Exception ex) {
            AuthAuditLogger.warnAuthFailure(
                    classifyAuthFailure(ex),
                    ClientIpExtractor.clientIp(httpReq),
                    request != null ? request.getUsername() : null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .cacheControl(CacheControl.noStore().mustRevalidate().cachePrivate().noTransform())
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .build();
        }
    }

    private String classifyAuthFailure(Exception ex) {
        String n = ex.getClass().getSimpleName();
        if (n.contains("BadCredentials") || n.contains("Invalid")) return "INVALID_CREDENTIALS";
        if (n.contains("Locked")) return "ACCOUNT_LOCKED";
        if (n.contains("Rate") || n.contains("TooMany")) return "RATE_LIMIT";
        return "AUTH_FAILURE";
    }
}
