package com.example.demo.security;

import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.example.demo.repository.UserRepository;
import com.example.demo.entity.User;

@Service
@RequiredArgsConstructor
/**
 * Adapter that loads `User` entities and converts them to Spring Security `UserDetails`.
 *
 * Main concept:
 * - Acts as the bridge between the application's `User` model and Spring Security.
 * - Provides user lookup by email (used as username) for authentication.
 *
 * Responsibilities:
 * - Implement `loadUserByUsername` to fetch a `User` from the repository and return a
 *   `UserDetails` instance containing username, password and authorities.
 */
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), Collections.singletonList(authority));
    }
}
