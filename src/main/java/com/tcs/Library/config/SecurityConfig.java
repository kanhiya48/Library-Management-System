
package com.tcs.Library.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.tcs.Library.enums.Role;
import com.tcs.Library.filter.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private CustomUserDetailService customUserDetailService;
    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static String p(String path) {
        return path;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .userDetailsService(customUserDetailService)
                .authorizeHttpRequests(auth -> auth
                        // Allow OPTIONS for CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, p("/**")).permitAll()
                        // Public endpoints
                        .requestMatchers(p("/auth/**"), p("/h2-console/**"), p("/whoami/**")).permitAll()
                        // Admin endpoints - accessible by ADMIN and STAFF (specific restrictions via
                        // method security)
                        .requestMatchers(p("/admin/**")).hasAnyRole(Role.ADMIN.name(), Role.STAFF.name())
                        // Staff complaint endpoints (staff or admin)
                        .requestMatchers(p("/staff/**")).hasAnyRole(Role.STAFF.name(), Role.ADMIN.name())
                        // User and authenticated endpoints
                        .requestMatchers(p("/complaints/**")).authenticated()
                        .requestMatchers(p("/books/**")).authenticated()
                        .requestMatchers(p("/authors/**")).authenticated()
                        .requestMatchers(p("/fines/**")).authenticated()
                        // Legacy endpoints
                        .requestMatchers(p("/user/search/**"), p("/author/register/**")).authenticated()
                        // All other requests require authentication
                        .anyRequest().authenticated());

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(
                List.of("http://127.0.0.1:5500", "http://localhost:5500",
                        "http://localhost:4200", "http://127.0.0.1:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Cache-Control"));
        config.setExposedHeaders(List.of(
                "Authorization",
                "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(p("/**"), config);
        return source;

    }
}
