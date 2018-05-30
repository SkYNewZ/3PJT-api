package com.supinfo.supdrive.service;

import com.supinfo.supdrive.exception.AppException;
import com.supinfo.supdrive.model.*;
import com.supinfo.supdrive.payload.SignUpRequest;
import com.supinfo.supdrive.repository.FolderRepository;
import com.supinfo.supdrive.repository.OffreRepository;
import com.supinfo.supdrive.repository.RoleRepository;
import com.supinfo.supdrive.repository.UserRepository;
import com.supinfo.supdrive.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.UUID;

@Component
public class AuthService {
    public AuthService() {
    }

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    OffreRepository offreRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    public User createUser(SignUpRequest signUpRequest){

        // Creating user's account
        Offre offre = offreRepository.findByName("BASIC")
            .orElseThrow(() -> new AppException("User Offer not set."));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));

        User user = new User(signUpRequest.getFirstName(), signUpRequest.getLastName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(userRole));
        user.setProvider("supdrive");
        user.setOffre(offre);
        user.setCurrentDataSize(0L);
        User result = userRepository.save(user);

        //create default home folder
        Folder home =  new Folder();
        home.setUuid(getUuid());
        home.setName("home");
        home.setUser(result);
        home.setDefaultDirectory(true);
        home.setMimeType("inode/directory");
        folderRepository.save(home);

        return result;
    }

    private UUID getUuid(){
        return UUID.randomUUID();
    }
}
