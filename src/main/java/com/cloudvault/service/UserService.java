package com.cloudvault.service;


import com.cloudvault.model.User;
import com.cloudvault.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public String registerUser(User user){
        userRepository.save(user);
        return "Registered succusfully";
    }

        public String loginUser(@RequestParam String email, @RequestParam String password, HttpSession session){
                Optional<User> user=userRepository.findByEmail(email);

            if(user.isPresent() && user.get().getPassword().equals(password)){
                session.setAttribute("email", user.get().getEmail());
                session.setAttribute("role", "USER");

                return "Login successfully";
            }
            else{
                return "invalid credential";
            }
        }

}
