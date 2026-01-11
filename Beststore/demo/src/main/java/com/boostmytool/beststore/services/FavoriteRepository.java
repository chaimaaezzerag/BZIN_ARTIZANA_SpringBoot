package com.boostmytool.beststore.services;

import com.boostmytool.beststore.models.AppUser;
import com.boostmytool.beststore.models.Favorite;
import com.boostmytool.beststore.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findByUser(AppUser user);

    Favorite findByUserAndProduct(AppUser user, Product product);

    void deleteByUserAndProduct(AppUser user, Product product);
}
