package com.boostmytool.beststore.controllers;

import com.boostmytool.beststore.models.Product;
import com.boostmytool.beststore.models.ProductDto;
import com.boostmytool.beststore.services.ProductsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Sort;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductsRepository repo;

    @Autowired
    private com.boostmytool.beststore.services.FavoriteRepository favoriteRepo;

    @Autowired
    private com.boostmytool.beststore.services.OrderRepository orderRepo;

    @GetMapping({ "", "/" })
    public String adminRedirect() {
        return "redirect:/admin/products";
    }

    @GetMapping("/products")
    public String showDashboard(Model model) {
        // Favoris
        List<com.boostmytool.beststore.models.Favorite> favorites = favoriteRepo.findAll();

        // Commandes à confirmer
        List<com.boostmytool.beststore.models.Order> orders = orderRepo.findAll();

        model.addAttribute("favorites", favorites);
        model.addAttribute("orders", orders);

        return "admin/index";
    }

    @GetMapping("/products/inventory")
    public String showInventory(Model model) {
        // Tous les produits pour la gestion
        List<Product> products = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "admin/inventory";
    }

    @PostMapping("/products/orders/confirm/{id}")
    public String confirmOrder(@PathVariable int id) {
        com.boostmytool.beststore.models.Order order = orderRepo.findById(id).orElse(null);
        if (order != null) {
            order.setStatus("CONFIRMED");
            orderRepo.save(order);
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/products/create")
    public String showCreatePage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "admin/create";
    }

    @PostMapping("/products/create")
    public String createProduct(
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result) {
        if (productDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "L'image est obligatoire"));
        }

        if (result.hasErrors()) {
            return "admin/create";
        }

        // Sauvegarder l'image
        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

        try {
            String uploadDir = Paths.get("public/images").toAbsolutePath().toString() + "/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(createdAt);
        product.setImageFileName(storageFileName);

        repo.save(product);

        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit")
    public String showEditPage(
            Model model,
            @RequestParam int id) {
        try {
            Product product = repo.findById(id).get();
            model.addAttribute("product", product);

            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("productDto", productDto);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/admin/products";
        }

        return "admin/edit";
    }

    @PostMapping("/products/edit")
    public String updateProduct(
            Model model,
            @RequestParam int id,
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result) {
        try {
            Product product = repo.findById(id).get();
            model.addAttribute("product", product);

            if (result.hasErrors()) {
                return "admin/edit";
            }

            if (!productDto.getImageFile().isEmpty()) {
                // Supprimer l'ancienne image
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());

                try {
                    Files.delete(oldImagePath);
                } catch (Exception ex) {
                    System.out.println("Exception: " + ex.getMessage());
                }

                // Sauvegarder la nouvelle image
                MultipartFile image = productDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }
                product.setImageFileName(storageFileName);
            }

            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());

            repo.save(product);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/products/delete")
    public String deleteProduct(
            @RequestParam int id) {
        try {
            Product product = repo.findById(id).get();

            // Supprimer l'image
            Path imagePath = Paths.get("public/images/" + product.getImageFileName());

            try {
                Files.delete(imagePath);
            } catch (Exception ex) {
                System.out.println("Exception: " + ex.getMessage());
            }

            repo.delete(product);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        return "redirect:/admin/products";
    }
}
