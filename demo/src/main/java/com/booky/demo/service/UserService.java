package com.booky.demo.service;

import com.booky.demo.dao.UserDAO;
import com.booky.demo.dao.UserRepository;
import com.booky.demo.dto.UserDTO;
import com.booky.demo.model.User;
import com.booky.demo.model.VerificationToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserDAO userDAO;
    private UserRepository userRepository;
    private VerificationTokenService verificationTokenService;
    private EmailService emailService;


    public UserService(UserDAO userDAO,
                       UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       VerificationTokenService verificationTokenService,
                       EmailService emailService) {
        this.userDAO = userDAO;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenService = verificationTokenService;
        this.emailService = emailService;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("USER")
                .disabled(!user.isEnabled())
                .build();
    }

    public UserDTO findUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));

        return new UserDTO(user.getId(), user.getName(), user.getSurname(), user.getUsername(), user.getEmail(),null);
    }

    @Transactional
    public Optional<Integer> register(UserDTO user) {
        Optional<Integer> existingId = userDAO.getIdByUsername(user.username());

        if(existingId.isPresent())
            return Optional.empty();

        User newUser = new User();
        newUser.setName(user.name());
        newUser.setSurname(user.surname());
        newUser.setUsername(user.username());
        newUser.setEmail(user.email());
        newUser.setPassword(passwordEncoder.encode(user.password()));

        Optional<Integer> id = Optional.of(userDAO.register(newUser));
        System.out.println(id.get());

        User savedUser = userRepository.getReferenceById(id.get());
        VerificationToken token = verificationTokenService.createToken(savedUser);
        emailService.sendVerificationEmail(newUser.getEmail(), token);

        return id;
    }

    @Transactional
    public boolean verifyToken(String token){
        Optional<User> user = verificationTokenService.verifyToken(token);
        if(user.isEmpty())
            return false;

        User us = user.get();
        us.setEnabled(true);
        userRepository.save(us);

        return true;
    }

//    @Transactional
//    public Optional<Integer> login(User user) {
//        System.out.println("logging in");
//        Optional<Integer> idOpt = userDAO.getIdByUsername(user.getUsername());
//
//        if(idOpt.isEmpty())
//            return Optional.empty();
//
//        Integer id = idOpt.get();
//        String storedHash = userDAO.getPasswordHashById(id);
//        if (!passwordEncoder.matches(user.getPassword(), storedHash))
//            return Optional.empty();
//
//        return Optional.of(id);
//    }

    @Transactional
    public ResponseEntity<UserDTO> updateProfile(User user, String name) {
        try {
            user.setId(userDAO.getIdByUsername(name).get());

            UserDTO updatedUser = userDAO.updateProfileDetails(user);

            UserDetails updatedDetails = loadUserByUsername(updatedUser.username());
            UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(updatedDetails, updatedDetails.getPassword(), updatedDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            return ResponseEntity.ok(updatedUser);       //200 OK
        }catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)         //409 Conflict
                    .body(null);
        }catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();    //404 Not Found
        }
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

}
