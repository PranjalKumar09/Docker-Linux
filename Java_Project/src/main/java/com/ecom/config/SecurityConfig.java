package com.ecom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

    @Configuration
    public class SecurityConfig {
    @Autowired
    private AuthSuccessHandlerImpl authSuccessHandler;
    @Autowired
    @Lazy
    private AuthFailtureHandlerImpl authFailtureHandler;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public DaoAuthenticationProvider authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return  authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF (use with caution in production)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/**").hasRole("USER")  // Requires USER role for /user/**
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // Requires ADMIN role for /admin/**
                        .requestMatchers("/**").permitAll()  // Allows all other requests
                )
                .formLogin(form -> form
                        .loginPage("/sginIn")  // Custom login page
                        .loginProcessingUrl("/login")  // Login form submission URL
                        .defaultSuccessUrl("/user/", true)  // Redirect to profile after successful login
                        .failureHandler(authFailtureHandler)
                        .successHandler(authSuccessHandler)  // Custom success handler
                        .permitAll()  // Allow public access to login page
                ).logout(LogoutConfigurer::permitAll  // Allow public access to logout
                );

        return http.build();
    }







}
