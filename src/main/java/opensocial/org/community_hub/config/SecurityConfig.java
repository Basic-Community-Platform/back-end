package opensocial.org.community_hub.config;

import opensocial.org.community_hub.filter.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // CORS 설정 적용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/login", "/api/users/register", "/api/users/refresh", "/swagger-ui/**", "/v3/api-docs/**").permitAll()  // 허용 경로 설정
                        .anyRequest().authenticated()  // 나머지 요청은 인증 필요
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 세션 비활성화 (JWT 사용)
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);  // JWT 필터 추가

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);  // 자격 증명 허용
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://board-project-two.vercel.app"));
        config.setAllowedMethods(Collections.singletonList("*"));  // 모든 HTTP 메서드 허용
        config.setAllowedHeaders(Collections.singletonList("*"));  // 모든 헤더 허용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // 모든 경로에 대해 CORS 설정 적용
        return source;
    }
}
