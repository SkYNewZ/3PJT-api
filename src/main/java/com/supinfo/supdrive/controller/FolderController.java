package com.supinfo.supdrive.controller;

import com.supinfo.supdrive.exception.ResourceNotFoundException;
import com.supinfo.supdrive.model.Folder;
import com.supinfo.supdrive.model.ResponseDto;
import com.supinfo.supdrive.model.User;
import com.supinfo.supdrive.repository.FolderRepository;
import com.supinfo.supdrive.repository.UserRepository;
import com.supinfo.supdrive.security.CurrentUser;
import com.supinfo.supdrive.security.UserPrincipal;
import com.supinfo.supdrive.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FolderController {

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FolderService folderService;

    // Create a new Folder
    @PostMapping({"/folder/{uuid}", "/folder"})
    public ResponseEntity<Folder> createFolder(@Valid @RequestBody Folder folder,
                                               @PathVariable(value = "uuid", required = false) UUID parentUuidFolder,
                                               @CurrentUser UserPrincipal currentUser) {

        User user = getUser(currentUser);
        Folder createdFolder = folderService.createFolder(folder, parentUuidFolder, user);
        return ResponseEntity.ok().body(createdFolder);
    }

    // get all folder's data ( by UUID )
    @GetMapping({"/folder/{uuid}", "/folder"})
    public ResponseEntity<ResponseDto> getFilesByFolder(@PathVariable(value = "uuid", required = false) UUID folderUuid,
                                                        @CurrentUser UserPrincipal currentUser) {

        // if folder is not specified, return the file is the current user's home directory
        User user = getUser(currentUser);
        ResponseDto responseDto = folderService.getFolderContent(folderUuid, user);
        return ResponseEntity.ok().body(responseDto);
    }

    // Update a Folder
    @PutMapping("/folder/{uuid}")
    public ResponseEntity<?> updateFolder(@PathVariable(value = "uuid") UUID folderUuid,
                               @Valid @RequestBody Folder folderUpdate,
                               @CurrentUser UserPrincipal currentUser) {

        User user = getUser(currentUser);
        Folder folder = folderRepository.findByUuidAndUser(folderUuid, user);
        if (folderUpdate.getName() == null){

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Folder name must be set.");
        }
        folder.setName(folderUpdate.getName());
        Folder updateFolder = folderRepository.save(folder);
        return ResponseEntity.ok().body(updateFolder);
    }

    // Move a Folder
    @PutMapping("/folder/move/{uuid}")
    public Folder moveFolder(@PathVariable(value = "uuid") UUID folderUuid,
                               @Valid @RequestBody Folder newFolder,
                               @CurrentUser UserPrincipal currentUser) {

        User user = getUser(currentUser);
        Folder folder = folderService.moveFolder(folderUuid, newFolder, user);
        return folder;
    }

    // Share a folder
    @PutMapping("/folder/share/{uuid}")
    public ResponseEntity<?> shareFolder(@PathVariable(value = "uuid") UUID folderUuid,
                             @Valid @RequestBody Folder newFolder,
                             @CurrentUser UserPrincipal currentUser) {

        User user = getUser(currentUser);
        if (!newFolder.getShared() != true || newFolder.getShared() != false){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("folder shared must be true or false");
        }
        Folder folder = folderService.shareFolder(folderUuid, newFolder, user);
        return ResponseEntity.ok().body(folder);
    }

    // Delete a Folder
    @DeleteMapping("/folder/{uuid}")
    public ResponseEntity<?> deleteFolder(@PathVariable(value = "uuid") UUID folderUuid,
                                          @CurrentUser UserPrincipal currentUser) {

        User user = getUser(currentUser);
        folderService.deleteFolder(folderUuid, user);
        return ResponseEntity.noContent().build();

    }

    private UUID getUuid(){
        UUID uuid = UUID.randomUUID();
        return uuid;
    }

    private User getUser(UserPrincipal currentUser){

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));
        return user;
    }

}