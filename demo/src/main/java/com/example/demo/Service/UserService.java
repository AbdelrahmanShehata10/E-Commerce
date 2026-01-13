package com.example.demo.Service;


import com.example.demo.Config.JwtService;
import com.example.demo.Controller.AuthenticationResponse;
import com.example.demo.DTO.Register_DTO;
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
        User user = new User();
        user.setUsername(userData.getUsername());
        user.setEmail(userData.getEmail());
        user.setPassword(passwordEncoder.encode(userData.getPassword()));
        user.setRole(Role.USER);
        urepo.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
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

    public ResponseEntity<User> updateUser(int id, User update) {
        var user = urepo.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        } else {
            if (update.getUsername() != null) user.setUsername(update.getUsername());
            if (update.getEmail() != null) user.setEmail(update.getEmail());
            // Add other fields as needed

            urepo.save(user);
            return ResponseEntity.ok(user);
        }
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