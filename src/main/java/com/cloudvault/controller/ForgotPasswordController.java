package com.cloudvault.controller;


import com.cloudvault.service.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/forgot-password")
public class ForgotPasswordController {

    @Autowired
    private ForgotPasswordService forgotPasswordService;


    @PostMapping("/request")
    public String requestOtp(@RequestParam("email") String email, Model model){

        try{
            String otp= forgotPasswordService.generateOtp(email);
            model.addAttribute("email",email);
            return "redirect:/forgot-password/reset-page?email="+email;
        }catch (Exception e){
           model.addAttribute("error",e.getMessage());
           return "forgot_email";
        }
    }
    @GetMapping("/reset-page")
    public String showResetPage(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "reset_password"; // yeh reset_password.html render karega
    }
    public void validatePassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\[\\]{};':\"\\\\|,.<>/?]).{6,}$";
        if(!password.matches(regex)){
            throw new RuntimeException("Password must be at least 6 characters and include a lowercase, uppercase, number, and special character!");
        }
    }


    @PostMapping("/reset")
    public String resetPassword(@RequestParam("email") String email,@RequestParam("otp") String otp,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword ,Model model) {
        validatePassword(newPassword);
        if(!newPassword.equals(confirmPassword)){
            model.addAttribute("error","Password donot match!!");
            model.addAttribute("email",email);
            return "reset_password";
        }
        try {
            forgotPasswordService.resetPassword(email, otp, newPassword);
            model.addAttribute("message", "Password reset successfully !! please login");
            return "login";
        } catch (Exception e){
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "reset_password";
        }
    }

}
