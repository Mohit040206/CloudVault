

package com.cloudvault.controller;

import com.cloudvault.model.User;
import com.cloudvault.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //  Registration endpoint
    @PostMapping("/register")
    public String userRegistration(@RequestParam String name,
                                   @RequestParam String email,
                                   @RequestParam String password,
                                   @RequestParam String phoneNo) {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhoneNo(phoneNo);

        // save user in DB
        userService.registerUser(user);

        // after successful register → go to done.html
        return "redirect:/login.html";
    }

    // Custom login endpoint
    @PostMapping("/login")
    public String userLogin(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session) {

        // delegate login check to service
        return userService.loginUser(email, password, session);
    }
    // In UserController
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login";
        }
        boolean success = userService.changePassword(email, oldPassword, newPassword);
        return success ? "redirect:/done" : "redirect:/login?error=password";
    }

}
