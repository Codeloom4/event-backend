package com.codeloon.ems.controller;

import com.codeloon.ems.dto.LoginDto;
import com.codeloon.ems.dto.ResetDto;
import com.codeloon.ems.model.AuthResponse;
import com.codeloon.ems.service.AuthService;
import com.codeloon.ems.service.UserService;
import com.codeloon.ems.util.ResponseBean;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('CLIENT')")
    public ResponseEntity<ResponseBean> resetPassword(@Valid @RequestBody ResetDto resetDto) {
        HttpStatus httpStatus = HttpStatus.BAD_GATEWAY;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = userService.resetPassword(resetDto);
            httpStatus = HttpStatus.OK;
        } catch (Exception exception) {
            log.error("Error occurred. error : {} ", exception.getMessage());
        }
        return new ResponseEntity<>(responseBean, httpStatus);
    }
}
