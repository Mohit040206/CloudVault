package com.cloudvault.controller;

import com.cloudvault.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private AdminService adminService;
    @PostMapping("/login")
    public String adminLogin(@RequestParam String email,
                                    @RequestParam String password, HttpSession session){
        return adminService.loginAdmin(email,password,session);

    }
}
