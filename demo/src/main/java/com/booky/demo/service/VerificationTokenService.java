package com.booky.demo.service;

import com.booky.demo.dao.VerificationTokenRepository;
import com.booky.demo.model.User;
import com.booky.demo.model.VerificationToken;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class VerificationTokenService {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Transactional
    public VerificationToken createToken(User user) {
        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusHours(24));

        return tokenRepository.save(token);
    }

    @Transactional
    public Optional<User> verifyToken(String tokenStr){
        VerificationToken token = tokenRepository.findByToken(tokenStr);
        if(token == null)
            return Optional.empty();

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            return Optional.empty();
        }

//        tokenRepository.delete(token);

        return Optional.of(token.getUser());
    }

}
