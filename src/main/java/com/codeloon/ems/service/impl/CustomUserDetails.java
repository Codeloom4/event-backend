package com.codeloon.ems.service.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {

    private final boolean forcePasswordChange;

    public CustomUserDetails(String username, String password, boolean enabled,
                             boolean accountNonExpired, boolean credentialsNonExpired,
                             boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities,
                             boolean forcePasswordChange) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.forcePasswordChange = forcePasswordChange;
    }

    public boolean isForcePasswordChange() {
        return forcePasswordChange;
    }
}
