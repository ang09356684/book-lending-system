package com.library.security;

import com.library.entity.User;
import com.library.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Custom UserDetailsService - Loads user information from database
 *
 * @author Library System
 * @version 1.0.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return createUserDetails(user);
    }

    /**
     * Create UserDetails from User entity
     */
    private UserDetails createUserDetails(User user) {
        // Set authorities based on user role
        String role = user.getRole() != null ? user.getRole().getName() : "MEMBER";
        
        // Only check is_verified for LIBRARIAN users
        boolean isDisabled = false;
        if ("LIBRARIAN".equals(role)) {
            isDisabled = !user.getIsVerified();
        }
        // For MEMBER users, always allow login regardless of is_verified status
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(isDisabled)
                .build();
    }
}
