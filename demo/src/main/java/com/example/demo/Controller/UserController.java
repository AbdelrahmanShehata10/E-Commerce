package com.example.demo.Controller;


import com.example.demo.Controller.AuthenticationResponse;
import com.example.demo.DTO.Register_DTO;
import com.example.demo.DTO.change_password_req;
import com.example.demo.DTO.login_DTO;
import com.example.demo.Models.User;
import com.example.demo.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public List<User> getAllUsers(@RequestParam(required = false) String sort) {
        return userService.getAllUsers(sort);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @PostMapping("/register")
    public AuthenticationResponse createUser(@RequestBody Register_DTO userData) {
        return userService.registerUser(userData);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody login_DTO loginRequest) {
        return userService.login(loginRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(name = "id") int id,
                                           @RequestBody User update) {
        return userService.updateUser(id, update);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") int id) {
        return userService.deleteUser(id);
    }

    @PostMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable(name = "id") int id,
                                               @RequestBody change_password_req request) {
        return userService.changePassword(id, request);
    }
}