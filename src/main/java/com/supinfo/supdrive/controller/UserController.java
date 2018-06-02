package com.supinfo.supdrive.controller;

import com.supinfo.supdrive.exception.ResourceNotFoundException;
import com.supinfo.supdrive.model.Offre;
import com.supinfo.supdrive.model.User;
import com.supinfo.supdrive.payload.UpdateUserRequest;
import com.supinfo.supdrive.repository.FilesRepository;
import com.supinfo.supdrive.repository.OffreRepository;
import com.supinfo.supdrive.repository.UserRepository;
import com.supinfo.supdrive.security.CurrentUser;
import com.supinfo.supdrive.security.UserPrincipal;
import com.supinfo.supdrive.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    OffreRepository offreRepository;

    @Autowired
    UserService userService;

    @Autowired
    FilesRepository filesRepository;

    //check username availabilty
    @GetMapping("/user/checkUsernameAvailability")
    public Boolean checkUsernameAvailability(@RequestParam String username) {
        return !(userRepository.existsByUsername(username));
    }

    @GetMapping("/user/me")
    public ResponseEntity<User> getUserInfo(@CurrentUser UserPrincipal currentUser) {
        User user = getUser(currentUser);
        if (filesRepository.sumByUserId(user.getId()) == null) {
            user.setCurrentDataSize(0L);
        }else {user.setCurrentDataSize(filesRepository.sumByUserId(user.getId()));}

        return ResponseEntity.ok().body(user);
    }

    // get all offre
    @GetMapping("/offres")
    public ResponseEntity<?> getAllOffre(){

        List<Offre> offres = offreRepository.selectAll();
        return ResponseEntity.ok().body(offres);
    }

    //update user
    @PutMapping("/user/me")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest newUser,
                                        @CurrentUser UserPrincipal currentUser) {

        User user = getUser(currentUser);
        user = userService.updateUser(newUser, user);
        return ResponseEntity.ok().body(user);
    }

    //update user Offer
    @PutMapping("/user/offer")
    public ResponseEntity<?> updateUserOffer(@RequestBody Offre offre,
                                             @CurrentUser UserPrincipal currentUser) {

        User user = getUser(currentUser);
        Offre newOffre = offreRepository.findByName(offre.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Offre", "id", offre.getId()));

        if (newOffre.getName()!= null) {
            user.setOffre(newOffre);
            userRepository.save(user);
        }else {
            return ResponseEntity.ok().body("This offer does not exist");
        }
        return ResponseEntity.ok().body("User " + user.getUsername() + " have now the " + newOffre.getName() + " Offer");
    }

    private User getUser(UserPrincipal currentUser){

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));
        return user;
    }

}