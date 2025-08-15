package com.cloudvault.controller;


import com.cloudvault.model.User;
import com.cloudvault.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String userRegistration(@RequestParam String name,
                                        @RequestParam String email,
                                            @RequestParam String password,
                                                @RequestParam String phoneNo){
        User user=new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhoneNo(phoneNo);
       return userService.registerUser(user);
    }
    @PostMapping("/login")
    public String userLogin(@RequestParam String email,
                            @RequestParam String password, HttpSession session){
        return userService.loginUser(email,password,session);
    }
}
