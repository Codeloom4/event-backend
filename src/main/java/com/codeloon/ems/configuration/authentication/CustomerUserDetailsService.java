package com.codeloon.ems.configuration.authentication;


import com.codeloon.ems.entity.User;
import com.codeloon.ems.repository.UserRepository;
import com.codeloon.ems.service.impl.CustomUserDetails;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Data
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private final CustomUserDetailsChecker customUserDetailsChecker;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username) .orElseThrow(() ->
                new UsernameNotFoundException("User not exists by Username: "+username));

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map((roles) -> new SimpleGrantedAuthority(roles.getName()))
                .collect(Collectors.toSet());

//        return new org.springframework.security.core.userdetails.User(
//                username,
//                user.getPassword(),
//                user.getEnabled(),
//                user.getAccountNonExpired(),
//                user.getCredentialsNonExpired(),
//                user.getAccountNonLocked(),
//                authorities
//        );

        CustomUserDetails userDetails = new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.getEnabled(),
                user.getAccountNonExpired(),
                user.getCredentialsNonExpired(),
                user.getAccountNonLocked(),
                authorities,
                user.getForcePasswordChange()
        );

        customUserDetailsChecker.check(userDetails);

        return userDetails;
    }

}
