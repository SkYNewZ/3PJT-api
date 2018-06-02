package com.supinfo.supdrive.service;

import com.supinfo.supdrive.model.User;
import com.supinfo.supdrive.payload.UpdateUserRequest;
import com.supinfo.supdrive.repository.OffreRepository;
import com.supinfo.supdrive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public User updateUser(UpdateUserRequest newUser, User user){

        User updateUser = user;
        updateUser.setFirstname(newUser.getFirstName() != null ? newUser.getFirstName() : user.getFirstname());
        updateUser.setEmail(newUser.getEmail() != null ? newUser.getEmail() : user.getEmail());
        updateUser.setLastname(newUser.getLastName() != null ? newUser.getLastName() : user.getLastname());
        updateUser.setPassword(newUser.getPassword() != null ? passwordEncoder.encode(newUser.getPassword()) : user.getPassword());
        userRepository.save(updateUser);

        return updateUser;
    }
}
