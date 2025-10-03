package com.cloudvault.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cloudvault.model.ForgotPasswordToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgotPasswordTokenRepository extends JpaRepository<ForgotPasswordToken, Long> {

    Optional<ForgotPasswordToken> findByEmail(String email);


    Optional<ForgotPasswordToken> findByEmailAndOtp(String email, String otp);

    void deleteByEmail(String email);
}
