package com.example.demo.Service;


import com.example.demo.Config.JwtService;
import com.example.demo.Controller.AuthenticationResponse;
import com.example.demo.DTO.Register_DTO;
import com.example.demo.DTO.UserUpdateDTO;
import com.example.demo.DTO.change_password_req;
import com.example.demo.DTO.login_DTO;
import com.example.demo.Models.Role;
import com.example.demo.Models.User;
import com.example.demo.Repo.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository urepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public List<User> getAllUsers(String sort) {
        Sort sorting = (sort != null && !sort.isBlank()) ? Sort.by(sort) : Sort.unsorted();
        return urepo.findAll(sorting);
    }

    public ResponseEntity<User> getUserById(int id) {
        var user = urepo.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(user);
        }
    }

    public AuthenticationResponse registerUser(Register_DTO userData) {


        if (urepo.findByUsername(userData.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }


        if (urepo.findByEmail(userData.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }


        if (!isValidEmail(userData.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }


        if (userData.getPassword() == null || userData.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }


        if (userData.getUsername() == null || userData.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (userData.getEmail() == null || userData.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }


        User user = new User();
        user.setUsername(userData.getUsername().trim());
        user.setEmail(userData.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(userData.getPassword()));
        user.setAge(userData.getAge());

        user.setRole(Role.USER);

        urepo.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    public AuthenticationResponse login(login_DTO loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        var user = urepo.findByUsername(loginRequest.getUsername()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        System.out.println(jwtToken);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
    public ResponseEntity<User> updateUser(int id, UserUpdateDTO update) {
        var user = urepo.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // ✅ FIX 1: Only update username if provided (not null)
        if (update.getUsername() != null && !update.getUsername().trim().isEmpty()) {
            Optional<User> existingUser = urepo.findByUsername(update.getUsername());

            // ✅ FIX 2: Check if exists AND belongs to someone else
            if (existingUser.isPresent() && !(existingUser.get().getId() == id) ) {
                throw new IllegalArgumentException("Username already exists");
            }

            if((update.getUsername().equalsIgnoreCase(user.getUsername()))){

                throw new IllegalArgumentException("Matches Old Username");

            }

            user.setUsername(update.getUsername().trim());
        }
        // ✅ If username is null, we simply don't update it (no error)

        // ✅ FIX 3: Only update email if provided (not null)
        if (update.getEmail() != null && !update.getEmail().trim().isEmpty()) {
            // Validate format first
            if (!isValidEmail(update.getEmail())) {
                throw new IllegalArgumentException("Invalid email format");
            }

            Optional<User> existingUser = urepo.findByEmail(update.getEmail());

            // ✅ FIX 4: Throw error when email EXISTS (isPresent), not when empty!
            if (existingUser.isPresent() && !(existingUser.get().getId() == id)) {
                throw new IllegalArgumentException("Email already exists");
            }

            if((update.getEmail().equalsIgnoreCase(user.getEmail()))){

                throw new IllegalArgumentException("Matches Old Username");

            }

            user.setEmail(update.getEmail().trim().toLowerCase());
        }
        // ✅ If email is null, we simply don't update it (no error)





        urepo.save(user);
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<Void> deleteUser(int id) {
        var user = urepo.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        } else {
            urepo.delete(user);
            return ResponseEntity.noContent().build();
        }
    }

    public ResponseEntity<Void> changePassword(int id, change_password_req request) {
        var user = urepo.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        } else if (!passwordEncoder.matches(request.getOldpassword(), user.getPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            user.setPassword(passwordEncoder.encode(request.getNewpassword()));
            urepo.save(user);
            return ResponseEntity.noContent().build();
        }
    }
}