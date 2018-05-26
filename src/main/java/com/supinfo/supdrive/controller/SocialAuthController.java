package com.supinfo.supdrive.controller;

import com.supinfo.supdrive.exception.AppException;
import com.supinfo.supdrive.model.*;
import com.supinfo.supdrive.payload.JwtAuthenticationResponse;
import com.supinfo.supdrive.payload.LoginRequest;
import com.supinfo.supdrive.repository.FolderRepository;
import com.supinfo.supdrive.repository.RoleRepository;
import com.supinfo.supdrive.repository.UserRepository;
import com.supinfo.supdrive.security.JwtTokenProvider;
import com.supinfo.supdrive.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class SocialAuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @PostMapping("/facebook/signin")
    public ResponseEntity<?> facebookAuthenticateUser(@Valid @RequestBody SocialAccessToken facebookAccessToken) {


        Facebook appRequestTemplate = new FacebookTemplate(facebookAccessToken.getAccessToken());
        User user = new User();

        String[] fields = {"id", "email"};
        SocialData userProfile = appRequestTemplate.fetchObject("me", SocialData.class, fields);
        user.setFacebookId(userProfile.getId());
        user.setProvider("facebook");
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));

        user.setRoles(Collections.singleton(userRole));
        if (!userRepository.existsByFacebookId(user.getFacebookId())) {
            createUser(user);
        }

        User result = userRepository.findByFacebookId(user.getFacebookId());
        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setId(result.getId());
        userPrincipal.setFacebookId(result.getFacebookId());

        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null,
                AuthorityUtils.createAuthorityList("ROLE_USER"));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));

    }

    @PostMapping("/google/signin")
    public ResponseEntity<?> googleAuthenticateUser(@Valid @RequestBody SocialAccessToken googleAccessToken) {

        Google appRequestTemplate = new GoogleTemplate(googleAccessToken.getAccessToken());
        User user = new User();

        user.setGoogleId(appRequestTemplate.userOperations().getUserInfo().getId());
        user.setProvider("google");
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));
        user.setRoles(Collections.singleton(userRole));

        if (!userRepository.existsByGoogleId(user.getGoogleId())) {
            createUser(user);
        }

        User result = userRepository.findByGoogleId(user.getGoogleId());
        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setId(result.getId());
        userPrincipal.setGoogleId(result.getGoogleId());

        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null,
                AuthorityUtils.createAuthorityList("ROLE_USER"));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    private void createUser(User user) {
        User result = userRepository.save(user);
        //create default home folder
        Folder home = new Folder();
        home.setUuid(getUuid());
        home.setName("home");
        home.setUser(result);
        home.setDefaultDirectory(true);
        home.setMimeType("inode/directory");
        folderRepository.save(home);
    }

    private UUID getUuid() {
        return UUID.randomUUID();
    }
}
