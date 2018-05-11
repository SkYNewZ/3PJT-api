package com.supinfo.supdrive.controller;

import com.supinfo.supdrive.exception.ResourceNotFoundException;
import com.supinfo.supdrive.model.User;
import com.supinfo.supdrive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserController userController;

    @Autowired
    UserRepository userRepository;

    // Get All User
    @GetMapping("/user")
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    // Create a new User
    @PostMapping("/user")
    public User createUser(@Valid @RequestBody User user) {
        return userRepository.save(user);
    }

    // Get a Single User
    public User getUserById(@PathVariable(value = "id") Long UserId) {
        return userRepository.findById(UserId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", UserId));
    }

    // Update a User
    @PutMapping("/user/{id}")
    public User updateUser(@PathVariable(value = "id") Long UserId,
                            @Valid @RequestBody User userDetails) {

        User user = userRepository.findById(UserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", UserId));

        user.setFirstname(userDetails.getFirstname());
        user.setLastname(userDetails.getLastname());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());

        User updatedUser = userRepository.save(user);
        return updatedUser;
    }

    // Delete a User
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable(value = "id") Long UserId) {
        User user = userRepository.findById(UserId)
                .orElseThrow(() -> new ResourceNotFoundException("user", "id", UserId));

        userRepository.delete(user);

        return ResponseEntity.ok().build();
    }

}