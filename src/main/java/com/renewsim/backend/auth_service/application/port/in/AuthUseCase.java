package com.renewsim.backend.auth_service.application.port.in;

import com.renewsim.backend.auth_service.web.dto.AuthRequestDTO;
import com.renewsim.backend.auth_service.web.dto.AuthResponseDTO;

public interface AuthUseCase {
    AuthResponseDTO login(AuthRequestDTO request);

    AuthResponseDTO register(AuthRequestDTO request);
}
