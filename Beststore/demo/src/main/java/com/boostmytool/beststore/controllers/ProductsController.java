package com.boostmytool.beststore.controllers;

import com.boostmytool.beststore.models.Product;
import com.boostmytool.beststore.services.ProductsRepository;
import com.boostmytool.beststore.services.UserRepository;
import com.boostmytool.beststore.services.FavoriteRepository;
import com.boostmytool.beststore.models.AppUser;
import com.boostmytool.beststore.models.Favorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductsRepository repo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private FavoriteRepository favoriteRepo;

    // Affiche la boutique
    @GetMapping({ "", "/" })
    public String showProductList(Model model, @RequestParam(required = false) String search,
            java.security.Principal principal, org.springframework.security.core.Authentication authentication) {

        // Redirection automatique pour l'admin vers son tableau de bord
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin";
        }
        List<Product> products;
        if (search != null && !search.isEmpty()) {
            // Recherche simple par nom ou marque
            products = repo.findAll().stream()
                    .filter(p -> p.getName().toLowerCase().contains(search.toLowerCase()) ||
                            p.getBrand().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        } else {
            products = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }

        if (principal != null) {
            AppUser user = userRepo.findByUsername(principal.getName());
            List<Integer> favoriteProductIds = favoriteRepo.findByUser(user).stream()
                    .map(f -> Integer.valueOf(f.getProduct().getId()))
                    .toList();
            model.addAttribute("favoriteIds", favoriteProductIds);
        }

        model.addAttribute("products", products);
        return "products/index";
    }

    @PostMapping("/favorite/{id}")
    @ResponseBody
    public String toggleFavorite(@PathVariable int id, java.security.Principal principal) {
        if (principal == null)
            return "Error: Not logged in";

        AppUser user = userRepo.findByUsername(principal.getName());
        Product product = repo.findById(id).orElse(null);

        if (product == null)
            return "Error: Product not found";

        Favorite favorite = favoriteRepo.findByUserAndProduct(user, product);
        if (favorite != null) {
            favoriteRepo.delete(favorite);
            return "Removed";
        } else {
            Favorite newFavorite = new Favorite();
            newFavorite.setUser(user);
            newFavorite.setProduct(product);
            favoriteRepo.save(newFavorite);
            return "Added";
        }
    }

    @GetMapping("/favorites")
    public String showFavorites(Model model, java.security.Principal principal) {
        if (principal == null)
            return "redirect:/login";

        AppUser user = userRepo.findByUsername(principal.getName());
        List<Product> favorites = favoriteRepo.findByUser(user).stream()
                .map(Favorite::getProduct)
                .toList();

        model.addAttribute("products", favorites);
        return "products/favorites";
    }

    // Affiche les détails d'un sac
    @GetMapping("/details")
    public String showDetails(Model model, @RequestParam int id) {
        Product product = repo.findById(id).get();
        model.addAttribute("product", product);
        return "products/details";
    }

    // Affiche le formulaire de paiement
    @GetMapping("/checkout")
    public String showCheckoutPage(@RequestParam int id, Model model) {
        try {
            Product product = repo.findById(id).get();
            model.addAttribute("product", product);
        } catch (Exception ex) {
            return "redirect:/products";
        }
        return "products/checkout";
    }

    @Autowired
    private com.boostmytool.beststore.services.OrderRepository orderRepo;

    // Gère la validation du paiement et redirige vers le succès
    @PostMapping("/checkout")
    public String processOrder(@RequestParam int id, java.security.Principal principal) {
        if (principal != null) {
            AppUser user = userRepo.findByUsername(principal.getName());
            Product product = repo.findById(id).orElse(null);

            if (product != null) {
                com.boostmytool.beststore.models.Order order = new com.boostmytool.beststore.models.Order();
                order.setUser(user);
                order.setProduct(product);
                order.setStatus("PENDING");
                orderRepo.save(order);
            }
        }
        return "redirect:/products/success";
    }

    // Affiche la page de succès avec ta phrase de luxe
    @GetMapping("/success")
    public String showSuccessPage() {
        return "products/success";
    }
}