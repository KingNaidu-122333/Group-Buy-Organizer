package com.groupbuyorganizer.config;

import com.groupbuyorganizer.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/webhooks/stripe"))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**", "/error", "/webhooks/stripe").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/groups/**", "/checkout/**").authenticated()
                .anyRequest().authenticated())
            .formLogin(form -> form.loginPage("/login")
                .failureUrl("/login?error")
                .successHandler((request, response, authentication) ->
                    response.sendRedirect(request.getContextPath() + landingPage(authentication)))
                .permitAll())
            .logout(logout -> logout.logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true).clearAuthentication(true)
                .deleteCookies("JSESSIONID").permitAll());
        return http.build();
    }

    private String landingPage(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())) ? "/admin" : "/";
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository repository) {
        return username -> repository.findByUsernameIgnoreCase(username)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername()).password(user.getPassword())
                        .authorities(user.getAuthorities()).build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
