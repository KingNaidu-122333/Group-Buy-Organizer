package com.groupbuyorganizer.config;

import com.groupbuyorganizer.model.GroupBuy;
import com.groupbuyorganizer.model.User;
import com.groupbuyorganizer.repository.GroupBuyRepository;
import com.groupbuyorganizer.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(GroupBuyRepository groupBuyRepository,
                              UserRepository userRepository,
                              PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                userRepository.save(new User("admin", "admin@example.com", passwordEncoder.encode("admin123"), User.Role.ADMIN));
            }

            if (groupBuyRepository.count() == 0) {
                groupBuyRepository.save(new GroupBuy(
                        "Organic Snack Box",
                        "Healthy snack bundle for the office team.",
                        "Ava",
                        "admin",
                        8,
                        3,
                        12.5,
                        "Open"
                ));

                groupBuyRepository.save(new GroupBuy(
                        "Home Office Setup",
                        "Ergonomic accessories and desk items for remote workers.",
                        "Leo",
                        "admin",
                        6,
                        2,
                        24.0,
                        "Open"
                ));

                groupBuyRepository.save(new GroupBuy(
                        "Summer Water Bottle Pack",
                        "Reusable bottles for a reusable lifestyle campaign.",
                        "Noah",
                        "admin",
                        10,
                        5,
                        8.75,
                        "Ready to Order"
                ));
            }
        };
    }
}
