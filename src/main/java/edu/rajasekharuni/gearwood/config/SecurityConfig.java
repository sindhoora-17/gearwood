package edu.rajasekharuni.gearwood.config;

import edu.rajasekharuni.gearwood.services.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/products/**",
                                "/register",
                                "/login",
                                "/css/**",
                                "/images/**"
                        ).permitAll()
                        .requestMatchers("/cart/**").authenticated()
                        .requestMatchers("/checkout/**").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            String redirect = request.getParameter("redirect");

                            if (isSafeLocalRedirect(redirect)) {
                                response.sendRedirect(redirect);
                            } else {
                                response.sendRedirect("/");
                            }
                        })
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/?logout")
                        .permitAll()
                )

                .userDetailsService(customUserDetailsService);

        return http.build();
    }

    private boolean isSafeLocalRedirect(String redirect) {
        return redirect != null
                && !redirect.isBlank()
                && redirect.startsWith("/")
                && !redirect.startsWith("//");
    }
}
