package com.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * MVC Controller for Web UI Page Rendering (Model-View-Controller).
 *
 * Architectural Note (Interview Prep):
 * - @Controller (unlike @RestController) tells Spring MVC that handler methods return
 *   the logical view name (e.g. "index") that the ViewResolver (Thymeleaf) resolves to an HTML template
 *   located in 'src/main/resources/templates/index.html'.
 * - The 'Model' object acts as the bridge that carries data from the backend to the frontend template.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("appName", "ApexStore E-Commerce");
        model.addAttribute("tagline", "Premium Quality Products, Seamless Shopping Experience");
        return "index";
    }
}
