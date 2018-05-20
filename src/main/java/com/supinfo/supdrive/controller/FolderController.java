package com.supinfo.supdrive.controller;


import com.supinfo.supdrive.exception.ResourceNotFoundException;
import com.supinfo.supdrive.model.Folder;
import com.supinfo.supdrive.model.User;
import com.supinfo.supdrive.repository.FolderRepository;
import com.supinfo.supdrive.repository.UserRepository;
import com.supinfo.supdrive.security.CurrentUser;
import com.supinfo.supdrive.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FolderController {

    @Autowired
    FolderController folderController;

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    UserRepository userRepository;

    // Get Folder by owner
   // @GetMapping("/folder/{folder}")
    //public List<Folder> getAllDataByOwner(@PathVariable(value = "owner") Owner owner){
      //  return folderRepository.findByOwner(owner);
   // }

    // Create a new Folder
    @PostMapping("/folder")
    public ResponseEntity<Folder> createFolder(@Valid @RequestBody Folder folder,
                                               @CurrentUser UserPrincipal currentUser) {

        User user = new User();
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        user.setFirstname(currentUser.getFirstName());
        user.setLastname(currentUser.getLastName());
        user.setEmail(currentUser.getEmail());

        folder.setUuid(getUuid());
        folder.setUser(user);
        folder.setDefaultDirectory(false);
        if (folder.getParentId() == null){
            folder = folderRepository.findByNameAndIsDefaultDirectoryAndUserId("home", true, user.getId());
        }
        folderRepository.save(folder);

        return ResponseEntity.ok().body(folder);
    }

    // Update a Folder
    @PutMapping("/folder/{id}")
    public Folder updateFolder(@PathVariable(value = "id") Long folderId,
                            @Valid @RequestBody Folder folderDetails) {

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder", "id", folderId));

        folder.setName(folderDetails.getName());

        Folder updateFolder = folderRepository.save(folder);
        return updateFolder;
    }

    // Delete a Files
    @DeleteMapping("/folder/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable(value = "id") Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder", "id", folderId));

        folderRepository.delete(folder);

        return ResponseEntity.ok().build();
    }

    public UUID getUuid(){
        UUID uuid = UUID.randomUUID();
        return uuid;
    }

}