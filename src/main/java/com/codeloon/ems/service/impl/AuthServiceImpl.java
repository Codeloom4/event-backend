package com.codeloon.ems.service.impl;

import com.codeloon.ems.configuration.authentication.JwtTokenProvider;
import com.codeloon.ems.dto.LoginDto;
import com.codeloon.ems.entity.User;
import com.codeloon.ems.model.AuthResponse;
import com.codeloon.ems.repository.UserRepository;
import com.codeloon.ems.service.AuthService;
import com.codeloon.ems.util.DataVarList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public ResponseEntity<AuthResponse> login(LoginDto loginDto) {
        String token = "";
        String userRole = null;
        User user = null;
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getUsername(),
                    loginDto.getPassword()
            ));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenProvider.generateToken(authentication);

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            userRole = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();

            // Fetch user details from repository
            user = userRepository.findByUsername(loginDto.getUsername()).orElse(null);
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (AuthenticationException e) {
            return handleAuthenticationException(e);
        }

        AuthResponse credentialsExpResponse = validateUserCredentialsStatus(loginDto.getUsername(), token, userRole);
        if (credentialsExpResponse != null) {
            return new ResponseEntity<>(credentialsExpResponse, HttpStatus.OK);
        }

        return this.buildResponse(token, userRole, DataVarList.SUCCESS_AUTH, DataVarList.AUTH_SUCCESS, HttpStatus.OK, user);
    }

    private AuthResponse validateUserCredentialsStatus(String userName, String token, String userRole) {
        try {
            User user = userRepository.findByUsername(userName).orElse(null);
            if (user != null) {
                boolean requiresPasswordReset = !user.getCredentialsNonExpired() || user.getForcePasswordChange();
                if (requiresPasswordReset) {
                    String errorMessage = !user.getCredentialsNonExpired() ?
                            DataVarList.FAILED_AUTH_CRED_EXPIRED :
                            DataVarList.FAILED_AUTH_FIRST_LOGIN;

                    String errorCode = DataVarList.AUTH_FAILED_RESET_PASSWORD;
                    log.warn(errorMessage);

                    return AuthResponse.builder()
                            .id(user.getId())  // Ensure user ID is included
                            .userName(user.getUsername())  // Ensure username is included
                            .accessCode(errorCode)
                            .accessToken(token)
                            .accessMsg(errorMessage)
                            .userRole(userRole)
                            .build();
                }
            }
        } catch (Exception exception) {
            log.error("Error validating user login.", exception);
        }
        return null;
    }


    private ResponseEntity<AuthResponse> buildResponse(String token, String userRole, String authMsg, String authStatus, HttpStatus httpStatus, User user) {
        AuthResponse authResponseDto = AuthResponse.builder()
                .id(user.getId()) // Add user ID
                .userName(user.getUsername()) // Add username
                .accessCode(authStatus)
                .accessToken(token)
                .accessMsg(authMsg)
                .userRole(userRole)
                .build();
        return new ResponseEntity<>(authResponseDto, httpStatus);
    }


    private ResponseEntity<AuthResponse> handleAuthenticationException(AuthenticationException e) {
        String accessMsg = "";
        String accessCode = DataVarList.AUTH_FAILED_ACCOUNT_ISSUE;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        if (e instanceof DisabledException) {
            accessMsg = DataVarList.FAILED_AUTH_ACC_BLOCKED;
            httpStatus = HttpStatus.FORBIDDEN;
        } else if (e instanceof LockedException) {
            accessMsg = DataVarList.FAILED_AUTH_ACC_LOCKED;
            httpStatus = HttpStatus.FORBIDDEN;
        } else if (e instanceof AccountExpiredException) {
            accessMsg = DataVarList.FAILED_AUTH_ACC_EXPIRED;
            httpStatus = HttpStatus.FORBIDDEN;
        } else if (e instanceof BadCredentialsException) {
            accessMsg = DataVarList.FAILED_AUTH_ACC_INVALIED;
            httpStatus = HttpStatus.UNAUTHORIZED;
        }
        return buildResponse("", null, accessMsg, accessCode, httpStatus, null);
    }

}
