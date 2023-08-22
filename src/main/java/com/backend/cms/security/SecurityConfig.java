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
        http.csrf().disable();
        http.cors();


        http.authorizeRequests()
                .antMatchers("/api/v1/users/{id}").authenticated() // Require authentication for the endpoint
                .antMatchers("/api/v1/admin").permitAll()
                .antMatchers("/api/v1/admin/initialize").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/users/**").authenticated() // Requires authentication
                .antMatchers(HttpMethod.PUT, "/api/v1/users/**").authenticated() // Requires authentication
                .antMatchers(HttpMethod.DELETE, "/api/v1/users/**").authenticated() // Requires authentication
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
