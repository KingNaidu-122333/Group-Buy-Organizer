package com.groupbuyorganizer.config;

import com.groupbuyorganizer.model.GroupBuy;
import com.groupbuyorganizer.model.User;
import com.groupbuyorganizer.repository.GroupBuyRepository;
import com.groupbuyorganizer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Bean
    CommandLineRunner initData(GroupBuyRepository groupBuyRepository,
                              UserRepository userRepository,
                              PasswordEncoder passwordEncoder) {
        return args -> {
            // Never ship a known default password. Configure APP_ADMIN_PASSWORD explicitly.
            if (adminPassword != null && !adminPassword.isBlank()
                    && userRepository.findByUsername(adminUsername).isEmpty()) {
                userRepository.save(new User(adminUsername, adminEmail,
                        passwordEncoder.encode(adminPassword), User.Role.ADMIN));
            }

            if (groupBuyRepository.count() == 0) {
                groupBuyRepository.save(new GroupBuy("Organic Snack Box",
                        "Healthy snack bundle for the office team.", "Ava", "system",
                        8, 3, 12.5, "Open"));
                groupBuyRepository.save(new GroupBuy("Home Office Setup",
                        "Ergonomic accessories and desk items for remote workers.", "Leo", "system",
                        6, 2, 24.0, "Open"));
                groupBuyRepository.save(new GroupBuy("Summer Water Bottle Pack",
                        "Reusable bottles for a reusable lifestyle campaign.", "Noah", "system",
                        10, 5, 8.75, "Ready to Order"));
            }
        };
    }
}
