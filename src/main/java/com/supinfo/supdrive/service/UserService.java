package com.supinfo.supdrive.service;

import com.supinfo.supdrive.model.User;
import com.supinfo.supdrive.repository.OffreRepository;
import com.supinfo.supdrive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserService {

    public UserService() {
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    OffreRepository offreRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public User updateUser(User newUser, User user){

        user.setFirstname(newUser.getFirstname() != null ? newUser.getFirstname() : user.getFirstname());
        user.setEmail(newUser.getEmail() != null ? newUser.getEmail() : user.getEmail());
        user.setLastname(newUser.getLastname() != null ? newUser.getLastname() : user.getLastname());
        user.setUsername(newUser.getUsername() != null ? newUser.getUsername() : user.getUsername());
        user.setPassword(newUser.getPassword() != null ? passwordEncoder.encode(newUser.getPassword()) : user.getPassword());
        userRepository.save(user);

        return user;
    }
}
