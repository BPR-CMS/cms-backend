package com.backend.cms.security;

import com.backend.cms.security.jwt.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();

        // Add the JwtTokenFilter before the standard Spring Security filters
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Set session creation policy
        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"status\":401,\"message\":\"Unauthorized\"}");
        });

        http.authorizeRequests()
                .antMatchers("/api/v1/users/login").permitAll()
                .antMatchers("/api/v1/users/{id}").authenticated() // Require authentication for the endpoint
                .antMatchers("/api/v1/admin").permitAll()
                .antMatchers("/api/v1/admin/initialize").permitAll()
                .antMatchers("/api/v1/utils/resetDatabase").permitAll()
                .antMatchers("/api/v1/collections").authenticated()
                .antMatchers("/api/v1/collections/{collectionId}/attributes").authenticated()
                .antMatchers("/api/v1/invitations/send").authenticated()
                .antMatchers("/api/v1/invitations/resend/{userId}").permitAll()
                .antMatchers("/api/v1/invitations/isTokenExpired/{userId}").permitAll()
                .antMatchers("/api/v1/invitations/validateToken").permitAll()
                .antMatchers("/api/v1/users/setPassword/{userId}").permitAll()
                .antMatchers("/api/v1/posts/{collectionId}").authenticated()
                .antMatchers(HttpMethod.GET, "/api/v1/users/**").authenticated() // Requires authentication
                .antMatchers(HttpMethod.PUT, "/api/v1/users/**").authenticated() // Requires authentication
                .antMatchers(HttpMethod.DELETE, "/api/v1/users/**").authenticated() // Requires authentication
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .permitAll();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/*")
                        .allowedOrigins("http://localhost:3000", "https://6570f98423196400093added--candid-malasada-4886cc.netlify.app")
                        .allowedMethods("")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    // Bean to provide password encoding functionality
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}