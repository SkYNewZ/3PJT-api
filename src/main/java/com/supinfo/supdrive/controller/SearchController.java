package com.supinfo.supdrive.controller;

import com.supinfo.supdrive.exception.ResourceNotFoundException;
import com.supinfo.supdrive.model.ResponseDto;
import com.supinfo.supdrive.model.User;
import com.supinfo.supdrive.repository.FilesRepository;
import com.supinfo.supdrive.repository.FolderRepository;
import com.supinfo.supdrive.repository.UserRepository;
import com.supinfo.supdrive.security.CurrentUser;
import com.supinfo.supdrive.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SearchController {

    public SearchController() {
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    FilesRepository filesRepository;

    @Autowired
    FolderRepository folderRepository;

    @GetMapping("/search")
    public ResponseEntity<?> getSearch(@RequestParam("q") String name,
                                       @CurrentUser UserPrincipal currentUser) {

        User user = getUser(currentUser);
        String queryName = "%" + name + "%";
        ResponseDto responseDto = new ResponseDto();
        responseDto.setFiles(filesRepository.findByName(queryName, user.getId()));
        responseDto.setFolders(folderRepository.findByName(queryName, user.getId()));

        return ResponseEntity.ok().body(responseDto);
    }

    private User getUser(UserPrincipal currentUser) {

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));
        return user;
    }

}
