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
    public String login(HttpSession session) {
        // If session exists, redirect to gallery
        if (session.getAttribute("email") != null) {
            return "redirect:/documents/gallery";
        }
        return "login"; // else show login page
    }


    @GetMapping("/register")
    public String register() {
        return "register";  // templates/register.html
    }
    @GetMapping("/upload")
    public String showUploadPage(HttpSession session) {
        // If session has email, show upload page, otherwise redirect to login
        if (session.getAttribute("email") == null) {
            return "redirect:/login";
        }
        return "upload"; // shows the form page
    }

    @GetMapping("/forgotPassword")
    public String showForgotPasswordPage() {
        return "forgot_email";  // templates/forgot_password.html
    }
    @GetMapping("/user/change-password")
    public String changePassword(){
        return "change_password";
    }

}
