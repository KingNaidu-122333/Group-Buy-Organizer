package com.groupbuyorganizer.config;

import com.groupbuyorganizer.model.GroupBuy;
import com.groupbuyorganizer.repository.GroupBuyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(GroupBuyRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new GroupBuy(
                        "Organic Snack Box",
                        "Healthy snack bundle for the office team.",
                        "Ava",
                        8,
                        3,
                        12.5,
                        "Open"
                ));

                repository.save(new GroupBuy(
                        "Home Office Setup",
                        "Ergonomic accessories and desk items for remote workers.",
                        "Leo",
                        6,
                        2,
                        24.0,
                        "Open"
                ));

                repository.save(new GroupBuy(
                        "Summer Water Bottle Pack",
                        "Reusable bottles for a reusable lifestyle campaign.",
                        "Noah",
                        10,
                        5,
                        8.75,
                        "Ready to Order"
                ));
            }
        };
    }
}
