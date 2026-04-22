package com.carsh.Carsh.config;

import com.carsh.Carsh.model.repository.UserRepository;
import com.carsh.Carsh.model.service.CustomUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.SavedRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(UserRepository userRepository, JwtAuthFilter jwtAuthFilter) {
        this.userRepository = userRepository;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/home", "/register", "/cars/**", "/h2-console/**", "/auth/login", "/admin/init").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/client/**").hasAnyRole("CLIENT", "ADMIN")
                .requestMatchers("/orders/new/**").authenticated()
                .requestMatchers("/orders/**").authenticated()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect("/auth/login?accessDenied");
                })
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect("/auth/login?loginRequired");
                })
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/cars", true)
                .successHandler((request, response, authentication) -> {
                    SavedRequest savedRequest = (SavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
                    String targetUrl = savedRequest != null ? savedRequest.getRedirectUrl() : "/cars";
                    response.sendRedirect(targetUrl);
                })
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "jwt_token")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
            );

        return http.build();
    }
}
