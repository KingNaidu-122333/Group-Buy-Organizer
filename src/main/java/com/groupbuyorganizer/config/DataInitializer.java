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
            // Never retain the old demo password. Provision the bootstrap administrator from an environment secret.
            User bootstrapAdmin = userRepository.findByUsername(adminUsername).orElse(null);
            if (adminPassword != null && !adminPassword.isBlank()) {
                if (bootstrapAdmin == null) {
                    bootstrapAdmin = new User(adminUsername, adminEmail,
                            passwordEncoder.encode(adminPassword), User.Role.ADMIN);
                } else {
                    bootstrapAdmin.setEmail(adminEmail);
                    bootstrapAdmin.setPassword(passwordEncoder.encode(adminPassword));
                    bootstrapAdmin.setRole(User.Role.ADMIN);
                }
                userRepository.save(bootstrapAdmin);
            } else if (bootstrapAdmin != null && bootstrapAdmin.getRole() == User.Role.ADMIN) {
                // An old admin/admin123 account must not remain privileged when no secret is configured.
                bootstrapAdmin.setRole(User.Role.USER);
                userRepository.save(bootstrapAdmin);
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
