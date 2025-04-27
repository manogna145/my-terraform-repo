package com.business.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.business.entities.Product;
import com.business.entities.User;
import com.business.loginCredentials.UserLogin;
import com.business.services.ProductService;
import com.business.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;

    // Show all products on the /userlogin page
    @GetMapping("/userlogin")
    public String allProduct(Model model) {
        List<Product> products = productService.getAllProducts(); // Or use productRepository.findAll() directly
        model.addAttribute("products", products);
        return "All_Product"; // Display list of all products
    }

    // Login page
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("userLogin", new UserLogin()); // Initialize an empty UserLogin object
        return "Login"; // Show login form
    }

    // Handle user login submission
    @PostMapping("/login")
    public String login(@ModelAttribute("userLogin") UserLogin userLogin, Model model) {
        boolean isAuthenticated = userService.authenticateUser(userLogin.getEmail(), userLogin.getPassword());

        if (isAuthenticated) {
            model.addAttribute("user", userLogin.getEmail()); // Store user info in session
            return "Dashboard"; // Redirect to the user dashboard after login
        } else {
            model.addAttribute("error", "Invalid credentials"); // Show error message
            return "Login"; // Stay on login page if authentication fails
        }
    }

    @PostMapping("/selectProducts")
    public String selectProducts(@RequestParam(value = "selectedProducts", required = false) List<Integer> selectedProductIds,
                                  HttpSession session,
                                  Model model) {
        if (selectedProductIds != null && !selectedProductIds.isEmpty()) {
            List<Product> selectedProducts = new ArrayList<>();
            for (Integer id : selectedProductIds) {
                Product product = productService.getProduct(id);
                if (product != null) {
                    selectedProducts.add(product);
                }
            }
            session.setAttribute("selectedProducts", selectedProducts);
            model.addAttribute("message", "Products selected successfully!");
        } else {
            model.addAttribute("message", "No products selected.");
        }
        return "All_Product"; // stay on same page after selecting
    }

    // View selected products
    @GetMapping("/selecting")
    public String selectedProduct(HttpSession session, Model model) {
        List<Product> selectedProducts = (List<Product>) session.getAttribute("selectedProducts");
        model.addAttribute("selectedProducts", selectedProducts);
        return "Product_Selected";
    }

    @GetMapping("/register")
    public String showRegistrationPage() {
        return "Register"; // make sure register.html is inside templates folder
    }

    // To handle form submit (POST method)
    @PostMapping("/register")
    public String registerUser(@RequestParam("fullname") String fullname,
                                @RequestParam("email") String email,
                                @RequestParam("password") String password,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model) {

        // ✅ Check if passwords match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "Register";
        }

        // ✅ Logic to save user to database here
        // userService.save(new User(fullname, email, password));

        model.addAttribute("success", "Registration successful! Please login.");
        return "Login"; // after successful registration, go to login page
    }
   

}
