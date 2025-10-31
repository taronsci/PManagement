package com.booky.demo.dao;

import com.booky.demo.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
}
