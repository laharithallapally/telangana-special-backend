package com.telanaganaspecial.config;

import com.telanaganaspecial.entity.Product;
import com.telanaganaspecial.entity.Role;
import com.telanaganaspecial.entity.User;
import com.telanaganaspecial.repository.ProductRepository;
import com.telanaganaspecial.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Disabled — real data has been migrated from Railway to Aiven.
        // Seeding logic kept below (commented out) in case it's needed again later.

        /*
        // ── Seed Products ──
        if (productRepository.count() == 0) {
            productRepository.save(Product.builder()
                    .name("Sarvapindi")
                    .description("Rice flour and chana dal crispy pancake, served with chutney")
                    .price(40.0)
                    .image("https://example.com/images/sarvapindi.jpg")
                    .available(true)
                    .category("Snacks")
                    .stock(50)
                    .isVeg(true)
                    .build());

            productRepository.save(Product.builder()
                    .name("Papu Vada")
                    .description("Crunchy dal vada rings, spiced and deep fried")
                    .price(30.0)
                    .image("https://example.com/images/papu-vada.jpg")
                    .available(true)
                    .category("Snacks")
                    .stock(50)
                    .isVeg(true)
                    .build());

            productRepository.save(Product.builder()
                    .name("Bobatlu")
                    .description("Sweet stuffed flatbread with jaggery and dal filling")
                    .price(50.0)
                    .image("https://example.com/images/bobatlu.jpg")
                    .available(true)
                    .category("Sweets")
                    .stock(30)
                    .isVeg(true)
                    .build());

            productRepository.save(Product.builder()
                    .name("Potato Twister")
                    .description("Spiral fried potato on a stick, masala spiced")
                    .price(60.0)
                    .image("https://example.com/images/potato-twister.jpg")
                    .available(true)
                    .category("Snacks")
                    .stock(40)
                    .isVeg(true)
                    .build());

            productRepository.save(Product.builder()
                    .name("Fruit Cream Bun")
                    .description("Soft bun loaded with fresh fruit and cream")
                    .price(45.0)
                    .image("https://example.com/images/fruit-cream-bun.jpg")
                    .available(true)
                    .category("Sweets")
                    .stock(25)
                    .isVeg(true)
                    .build());

            System.out.println("✅ Products seeded!");
        }

        // ── Seed Admin User ──
        if (!userRepository.existsByEmail("admin@telanganaspecial.com")) {
            User admin = User.builder()
                    .name("Admin")
                    .email("admin@telanganaspecial.com")
                    .password(passwordEncoder.encode("admin123"))
                    .phone("9000000000")
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Admin user created!");
        }
        */
    }
}