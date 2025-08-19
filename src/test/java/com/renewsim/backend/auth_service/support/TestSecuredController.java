package com.renewsim.backend.auth_service.support;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test-secure")
public class TestSecuredController {

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("ok-admin");
    }

    @GetMapping("/read-simulations")
    @PreAuthorize("hasAuthority('SCOPE_read:simulations')")
    public ResponseEntity<String> readSimulations() {
        return ResponseEntity.ok("ok-scope");
    }
}


