package com.codeloon.ems.service;

import com.codeloon.ems.model.AuthResponse;
import com.codeloon.ems.dto.LoginDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    /**
     * Authenticates the user with the provided login credentials.
     * If authentication is successful, returns a JWT access token.
     * If authentication fails, an appropriate error message, access code, and HTTP status code are returned.
     *
     * <p><b>Access Codes:</b></p>
     * <ul>
     *     <li><b>0</b> - Authentication successful, returns access token.</li>
     *     <li><b>1</b> - Password reset required, redirect to password reset page.</li>
     *     <li><b>2</b> - Account issue, user should contact admin.</li>
     * </ul>
     */
    ResponseEntity<AuthResponse> login(LoginDto loginDto);

}