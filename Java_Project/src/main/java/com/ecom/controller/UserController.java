package com.ecom.controller;

import com.ecom.entity.*;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;
import com.ecom.service.impl.ProductServiceImpl;
import com.ecom.util.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final CategoryService categoryService;
    private final CartService cartService;
    private final OrderService orderService;
    private final PasswordEncoder passwordEncoder;
    private final ProductServiceImpl productServiceImpl;

    public UserController(UserService userService, CategoryService categoryService, CartService cartService, OrderService orderService, PasswordEncoder passwordEncoder, ProductServiceImpl productServiceImpl) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.passwordEncoder = passwordEncoder;
        this.productServiceImpl = productServiceImpl;
    }


    @GetMapping("/")
    public String home() {
        return "user/home";
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

    @GetMapping("/addCart")
    public String addCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
//        System.out.println("Its in addcart");
        Cart cart = cartService.saveCart(pid, uid);
        if (cart == null)
            session.setAttribute("errorMsg", "Product add to cart failed");
        else {
            Product product = productServiceImpl.getProductById(pid);
            product.setStock(product.getStock() - 1);
            productServiceImpl.saveProduct(product);
            session.setAttribute("succMsg", "Product added to cart");
        }
        return "redirect:/view_product/"+pid;

    }

    @GetMapping("/cart")
    public String cart(Principal principal, Model model) {
//        System.out.println("In cart function");



        UserDtls userDtls = userService.getUserByEmail(principal.getName());
        List<Cart> cartList = cartService.getCartByUserId(userDtls.getId());
        model.addAttribute("cartList", cartList);
        if (!cartList.isEmpty()) {
        model.addAttribute("totalOrderPrice", cartList.getLast().getTotalOrderAmount());
        }
        return "user/cart";
    }
    @GetMapping("/cartQuantityUpdate")
    public String cartQuantityUpdate(@RequestParam String sy, @RequestParam Integer cartId) {
        cartService.updateQuantity(sy, cartId);

        return "redirect:/user/cart";
    }


    @GetMapping("order")
    public String order( Model model, Principal principal) {
        UserDtls userDtls = userService.getUserByEmail(principal.getName());
        List<Cart> cartList = cartService.getCartByUserId(userDtls.getId());
        model.addAttribute("cartList", cartList);
        if (!cartList.isEmpty()) {
            model.addAttribute("orderPrice", cartList.getLast().getTotalOrderAmount());
            model.addAttribute("totalOrderPrice", cartList.getLast().getTotalOrderAmount()+250+100);
        }


        return "user/order";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest orderRequest , Principal principal) {
        UserDtls user  = userService.getUserByEmail(principal.getName());
        orderService.saveOrder(user.getId(), orderRequest);

        return "redirect:/user/success";
    }

    @GetMapping("/success")
    public String success() {
        return "user/success";
    }


    @GetMapping("/my-orders")
    public String myorders(Model model, Principal principal,  @RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "1") Integer pageSize) {


        Page<ProductOrder> productOrderList = orderService.getOrderByUserId(userService.getUserByEmail(principal.getName()).getId(), pageNo,pageSize);
        model.addAttribute("productOrderList", productOrderList.getContent());
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", productOrderList.getTotalPages());
        model.addAttribute("totalElements", productOrderList.getTotalElements());
        model.addAttribute("isFirst", productOrderList.isFirst());
        model.addAttribute("isLast", productOrderList.isLast());
        return "user/my-orders";
    }
    @GetMapping("/update-status")
    public String updateStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {
        try {
            // Resolve the integer status to the corresponding enum constant
            OrderStatus orderStatus = Arrays.stream(OrderStatus.values())
                    .filter(os -> os.getId().equals(st))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid status ID: " + st));

            // Update the order status
            ProductOrder productOrder = orderService.updateOrderStatus(id, orderStatus.name());

            if (productOrder!=null) {
                session.setAttribute("succMsg", "Order status updated successfully.");
            } else {
                session.setAttribute("errorMsg", "Order not found. Unable to update status.");
            }
        } catch (IllegalArgumentException e) {
            session.setAttribute("errorMsg", e.getMessage());
        } catch (Exception e) {
            System.out.println(e);
            session.setAttribute("errorMsg", "An unexpected error occurred while updating the order status.");
        }

        // Redirect to the user's orders page
        return "redirect:/user/my-orders";
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        model.addAttribute("user", userService.getUserByEmail(principal.getName()));
        return "/user/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute UserDtls userDtls, @RequestParam MultipartFile image, HttpSession httpSession) {
        userService.updateUserProfile(userDtls, image, httpSession);

        return "redirect:/user/profile";
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


        return "redirect:/user/profile";
    }




}

