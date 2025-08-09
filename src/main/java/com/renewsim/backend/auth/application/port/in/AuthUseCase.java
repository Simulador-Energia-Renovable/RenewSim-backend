package com.renewsim.backend.auth.application.port.in;

import com.renewsim.backend.auth.web.dto.AuthRequestDTO;
import com.renewsim.backend.auth.web.dto.AuthResponseDTO;

public interface AuthUseCase {
    AuthResponseDTO login(AuthRequestDTO request);

    AuthResponseDTO register(AuthRequestDTO request);
}
