package com.ecom.controller;


import com.ecom.entity.Category;
import com.ecom.entity.Product;
import com.ecom.entity.ProductOrder;
import com.ecom.entity.UserDtls;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final CategoryService categoryService;
    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;
    private final CommonUtil commonUtil;
    private final PasswordEncoder passwordEncoder;

    public AdminController(CategoryService categoryService, ProductService productService, UserService userService, OrderService orderService, CommonUtil commonUtil, PasswordEncoder passwordEncoder) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.userService = userService;
        this.orderService = orderService;
        this.commonUtil = commonUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @ModelAttribute
    void getUserDetails(Principal principal, Model model) {
        if (principal != null) {
            UserDtls userDtls = userService.getUserByEmail(principal.getName());
            model.addAttribute("user", userDtls);
        }
        model.addAttribute("categoryList", categoryService.getAllActiveCategory());
    }

    @GetMapping("/")
    public String index() {
        return "admin/index";
    }



    @GetMapping("/add_product")
    public String add_product(Model model) {
        List<Category> categoryList = categoryService.getAllCategory();
        model.addAttribute("categoryList",categoryList);

        return "admin/add_product";
    }

    @PostMapping("/saveCategory")
    public String save_string(@ModelAttribute Category category,
                              @RequestParam("image_name") MultipartFile image,
                              HttpSession session) {
//        System.out.println(category);

        // Default image name in case no file is uploaded
        String imageName = "default.jpg";

        if (image != null && !image.isEmpty()) {
            imageName = image.getOriginalFilename();

            String uploadDir = "src/main/resources/static/img/category_img/";

            Path uploadPath = Paths.get(uploadDir);

            // Save the image to the directory
            try {
                assert imageName != null;
                Files.copy(image.getInputStream(), uploadPath.resolve(imageName), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                session.setAttribute("errorMsg", "Image upload failed.");
                return "redirect:/admin/category";
            }
        }

        // Set the image filename in the category object (not the MultipartFile)
        category.setImage(imageName);

        // Check if category already exists
        if (categoryService.existCategory(category.getTitle())) {
            session.setAttribute("errorMsg", "Category already exists.");
        } else {
            Category savedCategory = categoryService.saveCategory(category);
            if (savedCategory == null) {
                session.setAttribute("errorMsg", "Internal server error, category not saved.");
            } else {
                session.setAttribute("succMsg", "Castegory saved successfully.");
            }
        }

        return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession session) {
        if (categoryService.deleteCategory(id)){
            session.setAttribute("succMsg", "Category deleted successfully.");
        }
        else {
            session.setAttribute("errorMsg", "Internal server error, category not deleted.");
        }
        
        return "redirect:/admin/category";

    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "admin/edit_category";
    }


    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category,
                                 @RequestParam("image_name") MultipartFile image,
                                 HttpSession session) {
        // Fetch the existing category from the database to get the current image name
        Category existingCategory = categoryService.getCategoryById(category.getId());
        if (existingCategory == null) {
            session.setAttribute("errorMsg", "Category not found.");
            return "redirect:/admin/category";
        }

        // Determine the image name to use
        String imageName = existingCategory.getImage(); // Retain the current image by default

        // If a new image is uploaded, process and save it
        if (image != null && !image.isEmpty()) {
            imageName = image.getOriginalFilename();

            String uploadDir = "src/main/resources/static/img/category_img/";

            Path uploadPath = Paths.get(uploadDir);

            try {
                Files.copy(image.getInputStream(), uploadPath.resolve(imageName), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                session.setAttribute("errorMsg", "Image upload failed.");
                return "redirect:/admin/category";
            }
        }

        // Set the updated image name in the category object
        category.setImage(imageName);

        // Save the updated category
        Category updatedCategory = categoryService.saveCategory(category);
        if (updatedCategory == null) {
            session.setAttribute("errorMsg", "Internal server error, category not updated.");
        } else {
            session.setAttribute("succMsg", "Category updated successfully.");
        }

        return "redirect:/admin/category";
    }



    @PostMapping("/saveProduct")
    public String saveProduct(
            @ModelAttribute Product product,
            HttpSession session,
            @RequestParam("image_name") MultipartFile image) {

        // Determine the image name
        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
        product.setImage(imageName);
        product.setDiscount(0);
        product.setDiscount_price(product.getPrice());

        // Save the product
        Product savedProduct = productService.saveProduct(product);

        // Set appropriate session messages based on save result
        if (savedProduct == null) {
            session.setAttribute("errorMsg", "Product not saved.");
        } else {
            // Save the image if necessary
            if (!image.isEmpty()) {
                try {
                    Path uploadPath = Paths.get("src/main/resources/static/img/product_img/");

                    Files.copy(image.getInputStream(), uploadPath.resolve(imageName), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    session.setAttribute("errorMsg", "Product saved but failed to save image.");
                    return "redirect:/admin/add_product";
                }
            }
            session.setAttribute("succMsg", "Product saved successfully.");
        }

        return "redirect:/admin/add_product";
    }
        @GetMapping("/category")
    public String category(Model model, @RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "2") Integer pageSize){
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Category> categoryList = categoryService.getAllCategory(pageable);
        model.addAttribute("categoryList",categoryList);
        model.addAttribute("pageNo", pageNo); // pageNo = page.getNumber();
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", categoryList.getTotalPages());
        model.addAttribute("totalElements", categoryList.getTotalElements());
        model.addAttribute("isFirst", categoryList.isFirst());
        model.addAttribute("isLast", categoryList.isLast());

        return "admin/category";
    }


//    products -------------------------------------------

    @GetMapping("/products")
    public String loadViewProduct(Model model, @RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "5") Integer pageSize){
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Product> products = productService.getAllProduct(pageable);
        model.addAttribute("products",products.getContent());
        model.addAttribute("pageNo", pageNo); // pageNo = page.getNumber();
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("totalElements", products.getTotalElements());
        model.addAttribute("isFirst", products.isFirst());
        model.addAttribute("isLast", products.isLast());
        return "admin/products";
    }

    @GetMapping("/search-products")
    public String searchProducts(Model model, @RequestParam String keyword , @RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "5") Integer pageSize) {

        if (keyword != null) {
            Page<Product> products = productService.searchProduct(keyword.trim(), pageNo, pageSize);
            model.addAttribute("products", products.getContent());
            model.addAttribute("pageNo", pageNo); // pageNo = page.getNumber();
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalPages", products.getTotalPages());
            model.addAttribute("totalElements", products.getTotalElements());
            model.addAttribute("isFirst", products.isFirst());
            model.addAttribute("isLast", products.isLast());
        }

        return "/admin/products";
    }
//    --------------------------------------------------


//    orders -------------------------------------------
    @GetMapping("/orders")
    public String orders(Model model, @RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "5") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<ProductOrder> orderList = orderService.getOrders(pageable);
        model.addAttribute("orderList",orderList.getContent());
        model.addAttribute("pageNo", pageNo); // pageNo = page.getNumber();
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", orderList.getTotalPages());
        model.addAttribute("totalElements", orderList.getTotalElements());
        model.addAttribute("isFirst", orderList.isFirst());
        model.addAttribute("isLast", orderList.isLast());

        return "admin/orders";
    }

    @GetMapping("/search-orders")
    public String searchOrders(Model model, @RequestParam String keyword, @RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "2") Integer pageSize) {

        if (keyword != null) {
            Page<ProductOrder> orderList = orderService.seachOrders(keyword.trim(), pageNo, pageSize );

            model.addAttribute("orderList", orderList.getContent());
            model.addAttribute("pageNo", pageNo); // pageNo = page.getNumber();
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalPages", orderList.getTotalPages());
            model.addAttribute("totalElements", orderList.getTotalElements());
            model.addAttribute("isFirst", orderList.isFirst());
            model.addAttribute("isLast", orderList.isLast());
        }

            return "/admin/orders";
    }
//    -----------------------------------


    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id, HttpSession session) {
        if (productService.deleteProduct(id)) {
            session.setAttribute("succMsg", "Product deleted successfully.");
        }
        else {
            session.setAttribute("errorMsg", "Internal server error, category not deleted.");
        }
        return "redirect:/admin/products";
    }
   @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable int id, HttpSession session, Model model) {
       model.addAttribute("product", productService.getProductById(id));
       model.addAttribute("categoryList",categoryService.getAllCategory());

       return "admin/edit_product";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(
            @ModelAttribute Product product,
            HttpSession session,
            @RequestParam("image_name") MultipartFile image_name) {

        Product updatedProduct = productService.updateProduct(product, image_name);
        if (updatedProduct == null) {
            session.setAttribute("errorMsg", "Product not updated.");
        }
        else {
            session.setAttribute("succMsg", "Product updated successfully.");
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/users")
    public String getAllUses(Model model) {
        model.addAttribute("userList", userService.getUsers());
        return "admin/users";
    }
    @GetMapping("/admins")
    public String getAllAdmins(Model model) {
        model.addAttribute("userList", userService.getAdmins());
        return "admin/admins";
    }
    @GetMapping("/registerAdmin")
    public String registerAdmin( ) {
//        model.addAttribute("userList", userService.getUsers());
        return "admin/register_admin";
    }
    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        model.addAttribute("user", userService.getUserByEmail(principal.getName()));
        return "/admin/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute UserDtls userDtls, @RequestParam MultipartFile image, HttpSession httpSession) {
        userService.updateUserProfile(userDtls, image, httpSession);

        return "redirect:/admin/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, HttpSession session, Principal principal) {
        System.out.println("newPassword " + newPassword);
        System.out.println("currentPassword " + currentPassword);

        UserDtls userDtls = userService.getUserByEmail(principal.getName());
        if (passwordEncoder.matches(currentPassword, userDtls.getPassword())){
            userDtls.setPassword(newPassword);
            UserDtls userDtls1 = userService.saveUser(userDtls);

            if (userDtls1 != null) {
                session.setAttribute("succMsg", "Password changed successfully.");
            }
            else {
                session.setAttribute("errorMsg", "Password change failed.");
            }
        }
        else {
            session.setAttribute("errorMsg", "Password does not match.");
        }


        return "redirect:/admin/profile";
    }


    @PostMapping("/saveAdmin")
    public String saveAdmin(@ModelAttribute UserDtls userDtls, @RequestParam("img") MultipartFile img, HttpSession session ) {
        // Set a default image if no file is uploaded
        String imageName = img.isEmpty() ? "default.jpg" : img.getOriginalFilename();
        userDtls.setProfileImage(imageName);

        // Save the user details in the database
        UserDtls savedUser = userService.saveAdmin(userDtls);

        // Check if the user is saved and the image file is provided
        if (savedUser != null) {
            try {
                // Define the path to save the uploaded file
                String uploadDir = "src/main/resources/static/img/profile_img/";
                Path filePath = Paths.get(uploadDir, imageName);

                // Save the file to the server
                if (!img.isEmpty()) {
                    Files.write(filePath, img.getBytes());
                }

                // Set success message in the session
                session.setAttribute("succMsg", "Admin registered successfully!");

            } catch (IOException e) {
                // Log error if image saving fails
                e.printStackTrace();
                // Set error message in the session
                session.setAttribute("errorMsg", "Image upload failed");
                return "redirect:/admin/registerAdmin";
            }
        } else {
            // If user saving fails, set error message in the session
            session.setAttribute("errorMsg", "Admin registration failed");
            return "redirect:/admin/registerAdmin";

        }
        // Redirect to the registration page upon successful save
        return "redirect:/admin/registerAdmin";
    }
    @GetMapping("/updateStatus")
    public String updateUserAccountI(@RequestParam Boolean status , @RequestParam Integer id, HttpSession session) {
        if (userService.updateAccountStatus(id, status)){
            session.setAttribute("succMsg", "User account updated successfully.");
        }
        else {
            session.setAttribute("errorMsg", "Internal server error, category not updated.");
        }
        return "redirect:/admin/users";
    }




    @PostMapping("/update-order-status")
    public String updateStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {
        try {
            // Validate the status parameter
            if (st == null) {
                session.setAttribute("errorMsg", "Status is required.");
                return "redirect:/admin/orders";
            }

            // Resolve the integer status to the corresponding enum constant
            OrderStatus orderStatus = Arrays.stream(OrderStatus.values())
                    .filter(os -> os.getId().equals(st))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid status ID: " + st));

            // Update the order status
            ProductOrder productOrder = orderService.updateOrderStatus(id, orderStatus.name());


            if (productOrder!=null) {
                session.setAttribute("succMsg", "Order status updated successfully.");
                commonUtil.sendMailForProductOrder(productOrder);
            } else {
                session.setAttribute("errorMsg", "Order not found. Unable to update status.");
            }
        } catch (IllegalArgumentException e) {
            session.setAttribute("errorMsg", e.getMessage());
        } catch (Exception e) {
            session.setAttribute("errorMsg", "An unexpected error occurred while updating the order status.");
        }

        // Redirect to the user's orders page
        return "redirect:/admin/orders";
    }





}
