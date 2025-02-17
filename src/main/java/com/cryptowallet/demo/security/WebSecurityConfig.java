package com.cryptowallet.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        .antMatchers(HttpMethod.GET, "/api/transactions/**").hasAnyAuthority(ADMIN, USER)
                        .antMatchers(HttpMethod.POST, "/api/transactions").hasAnyAuthority(ADMIN, USER)
                        .antMatchers(HttpMethod.PUT, "/api/transactions/**").hasAnyAuthority(ADMIN, USER)
                        .antMatchers(HttpMethod.DELETE, "/api/transactions/**").hasAnyAuthority(ADMIN, USER)
                        .antMatchers(HttpMethod.GET, "/api/users/me").hasAnyAuthority(ADMIN, USER)
                        .antMatchers(HttpMethod.GET, "/api/users/**").hasAnyAuthority(ADMIN, USER)
                        .antMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .antMatchers(HttpMethod.PUT, "/api/users/**").hasAnyAuthority(ADMIN, USER)
                        .antMatchers(HttpMethod.DELETE, "/api/users/**").hasAnyAuthority(ADMIN, USER)
                        .antMatchers(HttpMethod.GET, "/api/wallets/**").hasAnyAuthority(ADMIN, USER)
                        .antMatchers(HttpMethod.GET, "/api/card/**", "/api/card").hasAnyAuthority(ADMIN, USER)
                        .antMatchers(HttpMethod.POST, "/api/wallets").hasAnyAuthority(ADMIN, USER)
                        .antMatchers(HttpMethod.PUT, "/api/wallets/**").hasAnyAuthority(ADMIN, USER)
                        .antMatchers(HttpMethod.DELETE, "/api/wallets/**").hasAnyAuthority(ADMIN, USER)
                        .antMatchers("/public/**", "/auth/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/exchange-rates/**", "/api/exchange-rates").permitAll()
                        .antMatchers("/", "/error", "/csrf", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()
                        .antMatchers("/api/stripe/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors().and()
                .csrf().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
}
