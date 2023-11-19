package com.app.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public WebSecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Disable CSRF (cross site request forgery)
        http.cors().and().csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeHttpRequests((authz) -> authz
                        .antMatchers(HttpMethod.POST, "/user/signup").permitAll()
                        .antMatchers(HttpMethod.POST, "/user/verifyEmail").permitAll()
                        .antMatchers(HttpMethod.POST, "/user/signin").permitAll()
                        .antMatchers(HttpMethod.POST, "/user/admin/signin").permitAll()
                        .antMatchers(HttpMethod.POST, "/user/search").permitAll()
//                        .antMatchers(HttpMethod.GET, "/user/{id}").permitAll()
//                        .antMatchers(HttpMethod.GET, "/user/all").permitAll()
                        .antMatchers(HttpMethod.POST, "/user/facebook/signin").permitAll()
                        .antMatchers(HttpMethod.POST, "/user/google/signin").permitAll()
                        .antMatchers(HttpMethod.POST, "/files/upload").permitAll()
                        .antMatchers(HttpMethod.GET, "/files/download/{fileCode}").permitAll()
                        .antMatchers(HttpMethod.POST, "/car/create-update").permitAll()
                        .antMatchers(HttpMethod.GET, "/car/{id}").permitAll()
                        .antMatchers(HttpMethod.GET, "/car/all").permitAll()
                        .antMatchers(HttpMethod.GET, "/car/all/projection").permitAll()
                        .antMatchers(HttpMethod.DELETE, "/car/id/{id}").permitAll()
                        .antMatchers(HttpMethod.POST, "/manufacturer/create-update").permitAll()
                        .antMatchers(HttpMethod.PUT, "/manufacturer/edit/{id}").permitAll()
                        .antMatchers(HttpMethod.GET, "/manufacturer/{id}").permitAll()
                        .antMatchers(HttpMethod.GET, "/manufacturer/all").permitAll()
                        .antMatchers(HttpMethod.DELETE, "/manufacturer/id/{id}").permitAll()
                        // Disallow everything else..
                        .anyRequest().authenticated()
                );

        // Apply JWT:
        http.apply(new JwtTokenFilterConfig(jwtTokenProvider));

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(Arrays.asList("localhost"));
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return corsConfigurationSource;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/v2/api-docs")
                .antMatchers("/swagger-resources/**")
                .antMatchers("/swagger-ui.html")
                .antMatchers("/configuration/**")
                .antMatchers("/webjars/**")
                .antMatchers("/public");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
