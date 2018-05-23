package com.supinfo.supdrive.controller;


import com.supinfo.supdrive.exception.ResourceNotFoundException;
import com.supinfo.supdrive.model.File;
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
import java.util.List;
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

    //param = name & parentId
    // Create a new Folder
    @PostMapping("/folder/{uuid}")
    public ResponseEntity<Folder> createFolder(@Valid @RequestBody Folder folder,
                                               @PathVariable(value = "uuid", required = false) UUID parentUuidFolder,
                                               @CurrentUser UserPrincipal currentUser) {

        User user = new User();
        user.setId(currentUser.getId());

        Folder parentFolder = folderRepository.findByUuidAndUser(parentUuidFolder, user);
        folder.setParentId(parentFolder.getId());
        folder.setDefaultDirectory(false);
        folder.setUuid(getUuid());
        folder.setUser(user);

        if (folder.getParentId() == null){
            Folder folder1 = folderRepository.findByNameAndIsDefaultDirectoryAndUserId("home", true, user.getId());
            folder.setParentId(folder1.getId());
        }
        folderRepository.save(folder);

        return ResponseEntity.ok().body(folder);
    }

    // get all folder's file ( by UUID )
    @GetMapping("/folder/{uuid}")
    public ResponseEntity<List<File>> getFilesByFolder(@CurrentUser UserPrincipal currentUser, @PathVariable(value = "uuid", required = false) UUID folderUuid) {
        // if folder is not specified, return the file is the current user's home directory
        User user = new User();
        user.setId(currentUser.getId());
        if (folderUuid != null) {
            Folder folder = folderRepository.findByUuidAndUser(folderUuid, user);
            return ResponseEntity.ok().body(folder.getFiles());
        }

        Folder folder = folderRepository.findByNameAndIsDefaultDirectoryAndUserId("home", true, currentUser.getId());
        return ResponseEntity.ok().body(folder.getFiles());
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

    private UUID getUuid(){
        UUID uuid = UUID.randomUUID();
        return uuid;
    }

}