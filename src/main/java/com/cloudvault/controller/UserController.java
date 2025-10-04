

package com.cloudvault.controller;

import com.cloudvault.model.User;
import com.cloudvault.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    public void validatePassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\[\\]{};':\"\\\\|,.<>/?]).{6,}$";
        if(!password.matches(regex)){
            throw new RuntimeException("Password must be at least 6 characters and include a lowercase, uppercase, number, and special character!");
        }
    }


    //  Registration endpoint
    @PostMapping("/register")
    public String userRegistration(@RequestParam String name,
                                   @RequestParam String email,
                                   @RequestParam String password,
                                   @RequestParam String phoneNo) {
        validatePassword(password);

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhoneNo(phoneNo);

        // save user in DB
        userService.registerUser(user);

        // after successful register → go to done.html
        return "redirect:/login";
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
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 Model model) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/login";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "change_password";
        }

        // Validate new password
        validatePassword(newPassword);

        boolean success = userService.changePassword(email, oldPassword, newPassword);
        if (success) {
            model.addAttribute("message", "Password changed successfully!");
            return "userhome";
        } else {
            model.addAttribute("error", "Old password is incorrect!");
            return "change_password";
        }
    }

    @GetMapping("/home")
       public String userHome(HttpSession session, Model model){
        String email=(String) session.getAttribute("email");
        if(email==null){
            return "redirect:/login";
        }

        String name=(String) session.getAttribute("name");
        String firstname=name!=null?name.split(" ")[0]:"";

        model.addAttribute("firstname",firstname);
        return "userhome";
        }
        @GetMapping("logout")
    public String logout(HttpSession session){
        session.invalidate();
            return "redirect:/login?logout=true";
        }
}
