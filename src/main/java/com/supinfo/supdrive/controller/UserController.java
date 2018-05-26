package com.supinfo.supdrive.controller;

import com.supinfo.supdrive.exception.ResourceNotFoundException;
import com.supinfo.supdrive.model.User;
import com.supinfo.supdrive.repository.UserRepository;
import com.supinfo.supdrive.security.CurrentUser;
import com.supinfo.supdrive.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserRepository userRepository;

    // TODO: Update a User
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

    //check username availabilty
    @GetMapping("/user/checkUsernameAvailability")
    public Boolean checkUsernameAvailability(@RequestParam String username) {
        return !(userRepository.existsByUsername(username));
    }

    @GetMapping("/user/me")
    public ResponseEntity<User> getUserInfo(@CurrentUser UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));
        return ResponseEntity.ok().body(user);
    }

}