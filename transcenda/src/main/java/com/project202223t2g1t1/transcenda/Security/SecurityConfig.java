package com.project202223t2g1t1.transcenda.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // this auto creates a constructor for the class with the final fields as parameters, thus no need for @Autowired aka constructor injection
public class SecurityConfig {


    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //cognito implementation
        //        http.csrf()
//                .and()
//                .authorizeRequests(authz -> authz.requestMatchers("/")
//                        .permitAll()
//                        .anyRequest()
//                        .authenticated())
//                .oauth2Login()
//                .and()
//                .logout()
//                .logoutSuccessUrl("/");
//        return http.build();

        //jwt implementation
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/transcenda/user/authenticate", "/api/v1/transcenda/user/registration")
                .permitAll()
                .anyRequest()
                .permitAll()//.authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
