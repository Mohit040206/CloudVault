package com.cloudvault.service;


import com.cloudvault.model.User;
import com.cloudvault.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
            session.setAttribute("name",user.get().getName());

            return "redirect:/user/home";
        } else {
            return "redirect:/login?error=true";
        }
    }
    // In UserService
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && encoder.matches(oldPassword, userOpt.get().getPassword())) {
            User user = userOpt.get();
            user.setPassword(encoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }

}
