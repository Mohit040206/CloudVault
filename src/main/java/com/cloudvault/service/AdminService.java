package com.cloudvault.service;


import com.cloudvault.model.Admin;
import com.cloudvault.repository.AdminRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;


public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Bean
    public String loginAdmin(@RequestParam String email,
                             @RequestParam String password,
                             HttpSession session) {
        Optional<Admin> admin = adminRepository.findByEmail(email);

        if (admin.isPresent() && admin.get().getPassword().equals(password)) {

                // Save session data
                session.setAttribute("email", admin.get().getEmail());
                session.setAttribute("role", "admin");

                //  Redirect to admin home
                return "Login successfully";
            } else {
                //  Invalid credentials
                return "invalid credential";
            }
        }
    }



