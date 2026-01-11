package com.boostmytool.beststore.controllers;

import com.boostmytool.beststore.models.AppUser;
import com.boostmytool.beststore.models.RegisterDto;
import com.boostmytool.beststore.services.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    @Autowired
    private UserRepository repo;

    @GetMapping("/register")
    public String register(Model model) {
        RegisterDto registerDto = new RegisterDto();
        model.addAttribute("registerDto", registerDto);
        model.addAttribute("success", false);
        return "register";
    }

    @PostMapping("/register")
    public String register(Model model, @Valid @ModelAttribute RegisterDto registerDto, BindingResult result) {
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            result.addError(new FieldError("registerDto", "confirmPassword", "Les mots de passe ne correspondent pas"));
        }

        AppUser appUser = repo.findByUsername(registerDto.getUsername());
        if (appUser != null) {
            result.addError(new FieldError("registerDto", "username", "Nom d'utilisateur déjà utilisé"));
        }

        appUser = repo.findByEmail(registerDto.getEmail());
        if (appUser != null) {
            result.addError(new FieldError("registerDto", "email", "Email déjà utilisé"));
        }

        if (result.hasErrors()) {
            return "register";
        }

        try {
            var bCryptEncoder = new BCryptPasswordEncoder();
            AppUser newUser = new AppUser();
            newUser.setUsername(registerDto.getUsername());
            newUser.setEmail(registerDto.getEmail());
            newUser.setRole("USER");
            newUser.setPassword(bCryptEncoder.encode(registerDto.getPassword()));

            repo.save(newUser);

            model.addAttribute("registerDto", new RegisterDto());
            model.addAttribute("success", true);
        } catch (Exception ex) {
            result.addError(new FieldError("registerDto", "username", "Erreur lors de l'enregistrement"));
        }

        return "register";
    }
}
