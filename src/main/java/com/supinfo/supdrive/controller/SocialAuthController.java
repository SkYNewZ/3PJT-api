package com.supinfo.supdrive.controller;

import com.supinfo.supdrive.model.SocialAccessToken;
import com.supinfo.supdrive.payload.JwtAuthenticationResponse;
import com.supinfo.supdrive.service.SocialAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class SocialAuthController {

    @Autowired
    SocialAuthService socialAuthService;

    @PostMapping("/facebook/signin")
    public ResponseEntity<?> facebookAuthenticateUser(@Valid @RequestBody SocialAccessToken facebookAccessToken) {

        String jwt = socialAuthService.loginWithFacebook(facebookAccessToken);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));

    }

    @PostMapping("/google/signin")
    public ResponseEntity<?> googleAuthenticateUser(@Valid @RequestBody SocialAccessToken googleAccessToken) {

        String jwt = socialAuthService.loginWithGoogle(googleAccessToken);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

}
