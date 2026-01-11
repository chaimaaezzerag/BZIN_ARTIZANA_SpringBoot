package com.boostmytool.beststore.services;

import com.boostmytool.beststore.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Integer> {
    AppUser findByUsername(String username);

    AppUser findByEmail(String email);
}
