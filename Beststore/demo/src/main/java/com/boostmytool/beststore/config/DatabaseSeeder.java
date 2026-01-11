package com.boostmytool.beststore.config;

import com.boostmytool.beststore.models.Product;
import com.boostmytool.beststore.services.ProductsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Date;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(ProductsRepository repo, com.boostmytool.beststore.services.UserRepository userRepo,
            org.springframework.security.crypto.password.PasswordEncoder encoder) {
        return args -> {
            // Création de l'admin par défaut si absent
            if (userRepo.findByUsername("admin") == null) {
                com.boostmytool.beststore.models.AppUser admin = new com.boostmytool.beststore.models.AppUser();
                admin.setUsername("admin");
                admin.setEmail("admin@beststore.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ADMIN");
                userRepo.save(admin);
            }

            if (repo.count() == 0) {
                repo.save(createProduct("Sac Birkin 25", "Hermès", "Maroquinerie", 12500,
                        "L'emblématique Birkin en cuir Togo noir.", "birkin.png"));
                repo.save(createProduct("Sac Lady Dior", "Dior", "Sacs portés main", 5200,
                        "Cuir d'agneau noir avec surpiqûre Cannage.", "lady_dior.png"));
                repo.save(createProduct("Sac Jackie 1961", "Gucci", "Sacs portés épaule", 2400,
                        "Le sac Jackie iconique revisité en cuir blanc.", "gucci_jackie.png"));
                repo.save(createProduct("Sac Hourglass", "Balenciaga", "Sacs portés main", 2100,
                        "Design curviligne unique en cuir noir brillant.", "hourglass.png"));
                repo.save(createProduct("Sac Baguette", "Fendi", "Pochettes", 2900,
                        "Sac Baguette emblématique en cuir nappa jaune.", "fendi_baguette.png"));
            }
        };
    }

    private Product createProduct(String name, String brand, String category, double price, String desc, String image) {
        Product p = new Product();
        p.setName(name);
        p.setBrand(brand);
        p.setCategory(category);
        p.setPrice(price);
        p.setDescription(desc);
        p.setImageFileName(image);
        p.setCreatedAt(new Date());
        return p;
    }
}
