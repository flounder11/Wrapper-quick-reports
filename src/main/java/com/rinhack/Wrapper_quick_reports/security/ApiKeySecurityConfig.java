//package com.rinhack.Wrapper_quick_reports.security;
//
//import com.rinhack.Wrapper_quick_reports.config.ApiKeyAuthenticationFilter;
//import com.rinhack.Wrapper_quick_reports.config.ApiKeyAuthenticationProvider;
//import com.rinhack.Wrapper_quick_reports.services.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.context.annotation.Primary;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.HttpStatusEntryPoint;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//
//@Configuration
//@EnableWebSecurity
//public class ApiKeySecurityConfig {
//    private UserService userService;
//
//    @Autowired
//    @Lazy
//    private TokenFilter tokenFilter;
//
//    @Autowired
//    private ApiKeyAuthenticationProvider apiKeyAuthenticationProvider;
//
//    public ApiKeySecurityConfig() {
//    }
//
//    @Autowired
//    public void setUserService(UserService userService) {
//        this.userService = userService;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//    @Bean
//    @Primary
//    public AuthenticationManagerBuilder configAuthenticationManagerBuilder(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder());
//        return authenticationManagerBuilder;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .cors(httpSecurityCorsConfigurer ->
//                        httpSecurityCorsConfigurer.configurationSource(request ->
//                                new CorsConfiguration().applyPermitDefaultValues())
//                )
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/auth/**").permitAll()
//                        .requestMatchers("/register").permitAll()
//                        .requestMatchers("/secured/user").hasRole("USER")
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(new ApiKeyAuthenticationFilter("X-API-KEY", "X-API-SECRET"), UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(apiKeyAuthenticationProvider);
//    }
//}