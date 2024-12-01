package com.ecom.controller;

import com.ecom.entity.Category;
import com.ecom.entity.Product;
import com.ecom.entity.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
public class HomeController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("categoryListLimited",categoryService.getAllCategory().stream().sorted((p1, p2) -> Long.compare(p2.getId(), p1.getId())) // Reverse sort by ID
                .limit(10)
                .toList());
        model.addAttribute("productListLimited",
                productService.getAllActiveProduct().stream()
                        .sorted((p1, p2) -> Long.compare(p2.getId(), p1.getId())) // Reverse sort by ID
                        .limit(10)
                        .toList()
        );




        return "index";
    }

    @GetMapping("/sginIn")
    public String login() {
        return "login";
    }
    @GetMapping("/register")
    public String register() {
        return "register";
    }


    // View products by category
    @GetMapping("/products")
    public String products(@RequestParam(defaultValue = "all") String category, Model model, @RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "2") Integer pageSize) {
        // Fetch all active categories
        List<Category> categoryList = categoryService.getAllActiveCategory();
        model.addAttribute("categoryList", categoryList);

        // Add the current category to the model
        model.addAttribute("currentCategory", category);

        Page<Product> productList = productService.getAllActiveProductPagination(pageNo, pageSize, category);
        model.addAttribute("productList", productList.getContent());
        model.addAttribute("pageNo", pageNo); // pageNo = page.getNumber();
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", productList.getTotalPages());
        model.addAttribute("totalElements", productList.getTotalElements());
        model.addAttribute("isFirst", productList.isFirst());
        model.addAttribute("isLast", productList.isLast());


        return "products";
    }
    @ModelAttribute
    void getUserDetails(Principal principal, Model model) {
        if (principal != null) {
            UserDtls userDtls = userService.getUserByEmail(principal.getName());
            model.addAttribute("user", userDtls);
            model.addAttribute("cartCount", cartService.getCountCart(userDtls.getId()));
        }
        model.addAttribute("categoryList", categoryService.getAllActiveCategory());
    }

    @GetMapping("/view_product/{id}")
    public String view_product(@PathVariable int id, Model model ) {
        model.addAttribute("product", productService.getProductById(id));
        return "view_product";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute UserDtls userDtls, @RequestParam("img") MultipartFile img, HttpSession session) {
        // Set a default image if no file is uploaded
        String imageName = img.isEmpty() ? "default.jpg" : img.getOriginalFilename();
        userDtls.setProfileImage(imageName);

        // Save the user details in the database
        UserDtls savedUser = userService.saveUser(userDtls);

        // Check if the user is saved and the image file is provided
        if (savedUser != null) {
            if (userService.existsByEmail(savedUser.getEmail())) {
                session.setAttribute("errorMsg", "User already exists");
                return "redirect:/register";
            }
            try {
                // Define the path to save the uploaded file
                String uploadDir = "src/main/resources/static/img/profile_img/";
                Path filePath = Paths.get(uploadDir, imageName);

                // Save the file to the server
                if (!img.isEmpty()) {
                    Files.write(filePath, img.getBytes());
                }

                // Set success message in the session
                session.setAttribute("succMsg", "User registered successfully!");

            } catch (IOException e) {
                // Log error if image saving fails
                e.printStackTrace();
                // Set error message in the session
                session.setAttribute("errorMsg", "Image upload failed");
                return "redirect:/register"; // Redirect back to registration page
            }
        } else {
            // If user saving fails, set error message in the session
            session.setAttribute("errorMsg", "User registration failed");
            return "redirect:/register";
        }
        // Redirect to the registration page upon successful save
        return "redirect:/register";
    }


    @GetMapping("/forgot_password")
    public String forgotPassword() {
        return "forgot_password";
    }

    @PostMapping("/process_forgot_password")
    public String process_forgot_password(@RequestParam String email, Model model, HttpSession session, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        UserDtls userDtls = userService.getUserByEmail(email);

        if (userDtls == null) {
            session.setAttribute("errorMsg", "Invalid email address");

        }
        else {

            String verificationCode = UUID.randomUUID().toString();
            String url = request.getRequestURL().toString().replace(request.getServletPath(), "") + "/reset-password?token="+verificationCode;
            userService.updateUserResetToken(email, verificationCode);


            if (CommonUtil.sendMail(url, email)){
                session.setAttribute("succMsg", "Please check your email.. Password link sent ");
            }
            else {
                session.setAttribute("errorMsg", "Something wrong in server");}

        }



        return "redirect:/forgot_password";
    }
    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam String token, HttpSession session, Model model) {
        UserDtls user = userService.getUserByToken(token);
        if (user == null) {
            model.addAttribute("msg", "Invalid token");
            return "message";
        }
        model.addAttribute("token", token);
        session.setAttribute("succMsg", "Correct token");
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String resetPasswordProcess(@RequestParam String token, @RequestParam String password, HttpSession session, Model model) {
        UserDtls user = userService.getUserByToken(token);
        if (user == null) {
            model.addAttribute("msg", "Invalid token");
            return "message";
        }
        user.setPassword(password);
        user.setVerificationCode(null);
        userService.saveUser(user);
        model.addAttribute("msg", "Password changed successfully");
        return "message";
    }



    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        List<Product> productList = productService.searchProduct(keyword);
        model.addAttribute("productList", productList);

        return "products";
    }




}
