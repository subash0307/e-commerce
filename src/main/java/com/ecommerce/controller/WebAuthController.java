package com.ecommerce.controller;

import com.ecommerce.dto.UserLoginDto;
import com.ecommerce.dto.UserRegistrationDto;
import com.ecommerce.entity.User;
import com.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class WebAuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/";
        }
        model.addAttribute("loginDto", new UserLoginDto());
        return "login";
    }

    @PostMapping("/login/submit")
    public String handleLogin(
            @ModelAttribute UserLoginDto loginDto,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.authenticate(loginDto);
            session.setAttribute("currentUser", user);
            redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + user.getFullName() + "!");
            if (user.getRole().name().equals("ROLE_ADMIN")) {
                return "redirect:/admin";
            }
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/";
        }
        model.addAttribute("registrationDto", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register/submit")
    public String handleRegister(
            @ModelAttribute UserRegistrationDto registrationDto,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.register(registrationDto);
            session.setAttribute("currentUser", user);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Welcome to ApexStore.");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "You have been logged out.");
        return "redirect:/login";
    }
}
