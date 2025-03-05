package com.codeloon.ems.configuration.authentication;

import com.codeloon.ems.service.impl.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomUserDetailsChecker implements UserDetailsChecker {

    @Override
    public void check(UserDetails userDetails) {
        if (!(userDetails instanceof CustomUserDetails)) {
            return;
        }

        CustomUserDetails customUser = (CustomUserDetails) userDetails;

        if (!customUser.isEnabled()) {
            log.warn("User account is disabled.");
            throw new DisabledException("User account is disabled.");
        }

        if (!customUser.isAccountNonLocked()) {
            log.warn("User account is locked.");
            throw new LockedException("User account is locked.");
        }

        if (!customUser.isAccountNonExpired()) {
            log.warn("User account has expired.");
            throw new AccountExpiredException("User account has expired.");
        }

//        if (!customUser.isCredentialsNonExpired()) {
//            log.warn("User credentials have expired.");
//            throw new CredentialsExpiredException("User credentials have expired.");
//        }
//
//        if (customUser.isForcePasswordChange()) {
//            log.warn("Password change required before logging in.");
//            throw new CredentialsExpiredException("Password change required before logging in.");
//        }
    }

}
