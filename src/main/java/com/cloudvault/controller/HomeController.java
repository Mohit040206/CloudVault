package com.cloudvault.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class HomeController{

    @GetMapping("/index")
    public String home() {
        return "index";  // templates/index.html
    }

    @GetMapping("/about")
    public String about() {
        return "about";  // templates/about.html
    }

    @GetMapping("/login")
    public String login() {
        return "login";  // templates/login.html
    }

    @GetMapping("/register")
    public String register() {
        return "register";  // templates/register.html
    }
    @GetMapping("/upload")
    public String showUploadPage(HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login.html";
        }
        return "upload"; // shows the form page
    }
    @GetMapping("/forgotPassword")
    public String showForgotPasswordPage() {
        return "forgot_email";  // templates/forgot_password.html
    }
//    @GetMapping("/forgot-password/reset-page")
//    public String resetPasswordPage() {
//        return "reset_password"; // templates/reset_password.html
//    }


}
