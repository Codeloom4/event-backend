package com.codeloon.ems.controller;

import com.codeloon.ems.model.AuthResponse;
import com.codeloon.ems.dto.LoginDto;
import com.codeloon.ems.dto.ResetDto;
import com.codeloon.ems.model.CommonResponse;
import com.codeloon.ems.service.AuthService;
import com.codeloon.ems.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDto loginDto) {
        return authService.login(loginDto);
    }

    @PostMapping("/reset")
    public ResponseEntity<CommonResponse> resetPassword(@Valid @RequestBody ResetDto resetDto) {
        return userService.resetPassword(resetDto);
    }
}
