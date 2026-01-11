package com.boostmytool.beststore.services;

import com.boostmytool.beststore.models.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserService implements UserDetailsService {

    @Autowired
    private UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = repo.findByUsername(username);

        if (appUser != null) {
            return User.withUsername(appUser.getUsername())
                    .password(appUser.getPassword())
                    .roles(appUser.getRole())
                    .build();
        }

        throw new UsernameNotFoundException("Utilisateur non trouvé : " + username);
    }
}
