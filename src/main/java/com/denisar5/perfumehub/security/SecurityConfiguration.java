package com.denisar5.perfumehub.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .userDetailsService(customUserDetailsService)

                .authorizeHttpRequests(authorize -> authorize

                        // Public static resources
                        .requestMatchers(
                                "/css/**",
                                "/images/**",
                                "/js/**",
                                "/favicon.ico"
                        ).permitAll()

                        // Public pages
                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/perfumes",
                                "/perfumes/**",
                                "/error"
                        ).permitAll()

                        // Admin-only area
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // Logged-in user area
                        .requestMatchers(
                                "/profile/**",
                                "/orders/**",
                                "/reviews/my/**"
                        ).authenticated()

                        // Any unlisted endpoint requires authentication
                        .anyRequest()
                        .authenticated()
                )

                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }
}