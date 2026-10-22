package com.universidad.perfumeria.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;

    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, AuthEntryPointJwt unauthorizedHandler) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> 
                auth.requestMatchers("/api/auth/**").permitAll() 
                    
                    // --- REGLAS PARA PERFUMES (INVENTARIO) ---
                    // Cajeros y Admins pueden ver la lista de perfumes para vender
                    .requestMatchers(HttpMethod.GET, "/api/perfumes/**").hasAnyRole("ADMIN", "CAJERO")
                    // SOLO los Admins pueden crear, editar o eliminar inventario
                    .requestMatchers(HttpMethod.POST, "/api/perfumes/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/perfumes/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/perfumes/**").hasRole("ADMIN")
                    
                    // --- REGLAS PARA VENTAS (CAJA) ---
                    // Cajeros y Admins pueden registrar una nueva venta
                    .requestMatchers(HttpMethod.POST, "/api/ventas/**").hasAnyRole("ADMIN", "CAJERO")
                    // SOLO los Admins pueden ver el historial de ventas globales y reportes
                    .requestMatchers(HttpMethod.GET, "/api/ventas/**").hasRole("ADMIN")
                    
                    .anyRequest().authenticated() 
            );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}