package com.cloudvault.controller;


import com.cloudvault.service.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/forgot-password")
public class ForgotPasswordController {

    @Autowired
    private ForgotPasswordService forgotPasswordService;


    @PostMapping("/request")
    public ResponseEntity<?> requestOtp(@RequestBody Map<String, String> request){
        String email=request.get("email");
        try{
            String otp= forgotPasswordService.generateOtp(email);
            return ResponseEntity.ok("Otp Generated Successfully. + " +otp);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String > request){
        String email=request.get("email");
        String otp=request.get("otp");
        String newPassword=request.get("newPassword");
        try{
            boolean success= forgotPasswordService.resetPassword(email,otp,newPassword);
            if(success){
                return ResponseEntity.ok("Password reset successfully");
            }
            else{
                return ResponseEntity.badRequest().body("Failed to reset password");
            }

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
