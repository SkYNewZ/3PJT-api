package com.supinfo.supdrive.service;

import com.supinfo.supdrive.exception.AppException;
import com.supinfo.supdrive.model.*;
import com.supinfo.supdrive.repository.FolderRepository;
import com.supinfo.supdrive.repository.OffreRepository;
import com.supinfo.supdrive.repository.RoleRepository;
import com.supinfo.supdrive.repository.UserRepository;
import com.supinfo.supdrive.security.JwtTokenProvider;
import com.supinfo.supdrive.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;

@Component
public class SocialAuthService {

    public SocialAuthService() {
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    OffreRepository offreRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;


    public String loginWithFacebook(SocialAccessToken facebookAccessToken){

        Offre offre = offreRepository.findByName("BASIC")
                .orElseThrow(() -> new AppException("User Offer not set."));
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));

        // check token
        Facebook appRequestTemplate = new FacebookTemplate(facebookAccessToken.getAccessToken());

        // get facebook info
        String[] fields = {"id", "email"};
        SocialData facebookUserProfile = appRequestTemplate.fetchObject("me", SocialData.class, fields);

        // create user with facebook
        User user = new User();
        user.setFacebookId(facebookUserProfile.getId());
        user.setProvider("facebook");
        user.setRoles(Collections.singleton(userRole));
        user.setOffre(offre);

        if (!userRepository.existsByFacebookId(user.getFacebookId())) {
            createUser(user);
        }

        // create user for authentification
        User result = userRepository.findByFacebookId(user.getFacebookId());
        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setId(result.getId());
        userPrincipal.setFacebookId(result.getFacebookId());

        String jwt = authentication(userPrincipal);
        return jwt;
    }

    public String loginWithGoogle(SocialAccessToken googleAccessToken){

        Offre offre = offreRepository.findByName("BASIC")
                .orElseThrow(() -> new AppException("User Offer not set."));
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));

        // check token
        Google appRequestTemplate = new GoogleTemplate(googleAccessToken.getAccessToken());

        //create user with google
        User user = new User();
        user.setGoogleId(appRequestTemplate.userOperations().getUserInfo().getId());
        user.setProvider("google");
        user.setRoles(Collections.singleton(userRole));
        user.setOffre(offre);

        if (!userRepository.existsByGoogleId(user.getGoogleId())) {
            createUser(user);
        }

        // create user for authentification
        User result = userRepository.findByGoogleId(user.getGoogleId());
        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setId(result.getId());
        userPrincipal.setGoogleId(result.getGoogleId());

        String jwt = authentication(userPrincipal);
        return jwt;
    }

    private String authentication(UserPrincipal userPrincipal){

        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null,
                AuthorityUtils.createAuthorityList("ROLE_USER"));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        return jwt;

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