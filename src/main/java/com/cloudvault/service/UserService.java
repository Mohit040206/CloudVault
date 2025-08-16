package com.cloudvault.service;


import com.cloudvault.model.User;
import com.cloudvault.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public void registerUser(User user){
        // encode password before saving
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public String loginUser(String email, String password, HttpSession session){
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent() && encoder.matches(password, user.get().getPassword())) {
            session.setAttribute("email", user.get().getEmail());
            session.setAttribute("role", "USER");

            return "redirect:/done.html";
        } else {
            return "redirect:/login.html?error=true";
        }
    }
}
