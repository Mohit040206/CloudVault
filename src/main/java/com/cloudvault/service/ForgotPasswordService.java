package com.cloudvault.service;

import com.cloudvault.model.ForgotPasswordToken;
import com.cloudvault.model.User;
import com.cloudvault.repository.ForgotPasswordTokenRepository;
import com.cloudvault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class ForgotPasswordService {
    @Autowired
    ForgotPasswordTokenRepository tokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MailService mailService;

    public String generateOtp(String email){

        Optional<User> userOpt=userRepository.findByEmail(email);
        if(userOpt.isEmpty()){
            throw new RuntimeException("User Not Found with this Email: " + email);
        }
        User user=userOpt.get();
        String otp=String.format("%06d", new Random().nextInt(999999));

        ForgotPasswordToken token=new ForgotPasswordToken();
        token.setEmail(email);
        token.setOtp(otp);
        token.setExpiryTime(LocalDateTime.now().plusMinutes(10)); // valid for 10 mins
        tokenRepository.save(token);

        mailService.sendOtpEmail(email,otp);


        return otp;
    }

    public boolean resetPassword(String email, String otp, String newPassword){
        Optional<User> userOpt=userRepository.findByEmail(email);
        if(userOpt.isEmpty()){
            throw new RuntimeException("User Not Found With this Email: "+ email);
        }
        User user=userOpt.get();
        Optional<ForgotPasswordToken> tokenOpt=tokenRepository.findByEmailAndOtp(user.getEmail(),otp);
        if(tokenOpt.isPresent()) {
            ForgotPasswordToken token = tokenOpt.get();
            if (token.getExpiryTime().isAfter(LocalDateTime.now())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);

//                delete the token
                tokenRepository.delete(token);
                return true;
            } else {
                throw new RuntimeException("OTP EXPIRED");
            }
        }
            else{
                throw new RuntimeException("INVALID OTP");
            }

    }


}
