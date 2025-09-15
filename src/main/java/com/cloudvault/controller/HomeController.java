package com.cloudvault.controller;

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
}
