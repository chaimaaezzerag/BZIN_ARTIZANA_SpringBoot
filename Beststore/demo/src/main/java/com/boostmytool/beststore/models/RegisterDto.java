package com.boostmytool.beststore.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size; // Added this import for @Size annotation

public class RegisterDto {
    @NotEmpty(message = "Le nom d'utilisateur est obligatoire")
    private String username;

    @NotEmpty(message = "L'email est obligatoire")
    @Email
    private String email;

    @Size(min = 6, message = "Le mot de passe doit faire au moins 6 caractères")
    private String password;

    private String confirmPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
