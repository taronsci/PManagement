package com.booky.demo.service;

import com.booky.demo.dao.UserDAO;
import com.booky.demo.dao.UserRepository;
import com.booky.demo.dto.UserDTO;
import com.booky.demo.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserDAO userDAO;

    @BeforeEach
    void setup(){
        SecurityContextHolder.clearContext();
    }

    // findUserByUsername
    @Test
    void testFindUserByUsername_Success() {
        User user = new User();
        user.setId(1);
        user.setUsername("taronsci");
        user.setEmail("taronsci@example.com");

        when(userRepository.findByUsername("taronsci")).thenReturn(Optional.of(user));

        UserDTO result = userService.findUserByUsername("taronsci");

        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals("taronsci", result.username());
        assertEquals("taronsci@example.com", result.email());
    }

    @Test
    void testFindUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.findUserByUsername("unknown")
        );

        assertEquals("User not found", exception.getMessage());
    }

    //loadUserByUsername
    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        String username = "alice";
        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPass");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("encodedPass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("USER")));
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        String username = "bob";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(username)
        );

        assertEquals("User not found", ex.getMessage());
    }

    //register
    @Test
    void register_ShouldReturnEmpty_WhenUserAlreadyExists() {
        UserDTO userDTO = new UserDTO(0, "taronsci", "taronsci@example.com", "pass123");
        when(userDAO.getIdByUsername("taronsci")).thenReturn(Optional.of(1));

        Optional<Integer> result = userService.register(userDTO);

        assertTrue(result.isEmpty());
        verify(userDAO, never()).register(any());
    }

    @Test
    void register_ShouldReturnId_WhenUserDoesNotExist() {
        UserDTO userDTO = new UserDTO(0, "megan", "megan@example.com", "pass123");

        when(userDAO.getIdByUsername("megan")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        when(userDAO.register(any(User.class))).thenReturn(42);

        Optional<Integer> result = userService.register(userDTO);

        assertTrue(result.isPresent());
        assertEquals(42, result.get());
    }

    //login
    @Test
    void login_ShouldReturnEmpty_WhenUsernameNotFound() {
        User user = new User();
        user.setUsername("unknown");

        when(userDAO.getIdByUsername("unknown")).thenReturn(Optional.empty());

        Optional<Integer> result = userService.login(user);

        assertTrue(result.isEmpty(), "Expected empty Optional when username not found");
        verify(userDAO, never()).getPasswordHashById(anyInt());
    }

    @Test
    void login_ShouldReturnEmpty_WhenPasswordIncorrect() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("wrongpass");

        when(userDAO.getIdByUsername("john")).thenReturn(Optional.of(10));
        when(userDAO.getPasswordHashById(10)).thenReturn("storedHash");
        when(passwordEncoder.matches("wrongpass", "storedHash")).thenReturn(false);

        Optional<Integer> result = userService.login(user);

        assertTrue(result.isEmpty(), "Expected empty Optional when password does not match");
    }

    @Test
    void login_ShouldReturnId_WhenUsernameAndPasswordCorrect() {
        User user = new User();
        user.setUsername("lily");
        user.setPassword("correctpass");

        when(userDAO.getIdByUsername("lily")).thenReturn(Optional.of(42));
        when(userDAO.getPasswordHashById(42)).thenReturn("storedHash");
        when(passwordEncoder.matches("correctpass", "storedHash")).thenReturn(true);

        Optional<Integer> result = userService.login(user);

        assertTrue(result.isPresent(), "Expected Optional with ID when login successful");
        assertEquals(42, result.get(), "Expected returned ID to match DAO value");
    }

    //updateProfile
    @Test
    void updateProfile_ShouldReturnOk_WhenUpdateSuccessful() {
        User user = new User();
        user.setUsername("alice");

        String name = "alice";

        UserDTO updatedDTO = new UserDTO(1, "alice", "alice@example.com", "encodedPass");

        when(userDAO.getIdByUsername(name)).thenReturn(Optional.of(1));
        when(userDAO.updateProfileDetails(user)).thenReturn(updatedDTO);

        User repoUser = new User();
        repoUser.setUsername(updatedDTO.username());
        repoUser.setPassword(updatedDTO.password());
        when(userRepository.findByUsername(updatedDTO.username())).thenReturn(Optional.of(repoUser));

        ResponseEntity<UserDTO> response = userService.updateProfile(user, name);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedDTO, response.getBody());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(updatedDTO.username(), auth.getName());
    }

    @Test
    void updateProfile_ShouldReturnConflict_WhenIllegalArgument() {
        User user = new User();
        String name = "greg";

        when(userDAO.getIdByUsername(name)).thenThrow(new IllegalArgumentException("Username conflict"));

        ResponseEntity<UserDTO> response = userService.updateProfile(user, name);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void updateProfile_ShouldReturnNotFound_WhenUserDoesNotExist() {
        User user = new User();
        String name = "charlie";

        when(userDAO.getIdByUsername(name)).thenThrow(new EmptyResultDataAccessException(1));

        ResponseEntity<UserDTO> response = userService.updateProfile(user, name);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

}
