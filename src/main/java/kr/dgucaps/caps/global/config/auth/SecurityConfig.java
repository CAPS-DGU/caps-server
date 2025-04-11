package kr.dgucaps.caps.global.config.auth;

import kr.dgucaps.caps.domain.member.service.KakaoOAuth2UserService;
import kr.dgucaps.caps.global.config.CorsConfig;
import kr.dgucaps.caps.global.config.auth.jwt.JwtAuthenticationEntryPoint;
import kr.dgucaps.caps.global.config.auth.jwt.JwtAuthenticationFilter;
import kr.dgucaps.caps.global.config.auth.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final KakaoOAuth2UserService kakaoOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CorsConfig corsConfig;

    // 토큰 없이 접근 가능한 URL
    private static final String[] whiteList = {"/",
            /* swagger */
            "swagger/**",
            "swagger-ui/**",
            "v3/api-docs/**",

            /* api */
            "api/auth/reissue",
            "api/v1/wikis/*",
            "api/v1/wikis/recent",
            "api/v1/wikis/random",
            "api/v1/wikis/*/history",
    };

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(whiteList);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(kakaoOAuth2UserService))
                        .successHandler(customOAuth2SuccessHandler))
                .sessionManagement(sessionManagementConfigurer ->
                        sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                                .requestMatchers(whiteList).permitAll()
                                .anyRequest().authenticated()
                )
                .addFilter(corsConfig.corsFilter())
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new ExceptionHandlerFilter(), JwtAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}