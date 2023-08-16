package com.backend.cms.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable CSRF protection (Cross-Site Request Forgery)
        http.csrf().disable();
        http.cors();
        // Authorize requests configuration
        http
                .authorizeRequests()
                .antMatchers("/api/v1/initialize").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/initialize/**").permitAll() // Allow GET requests for the /api/v1/initialize endpoint
                .antMatchers(HttpMethod.PUT, "/api/v1/initialize/**").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/v1/initialize/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .permitAll();
    }

    // Bean to provide password encoding functionality
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
