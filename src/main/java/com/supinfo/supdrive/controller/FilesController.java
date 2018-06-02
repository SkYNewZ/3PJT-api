package com.supinfo.supdrive.controller;

import com.supinfo.supdrive.exception.ResourceNotFoundException;
import com.supinfo.supdrive.exception.StorageFileNotFoundException;
import com.supinfo.supdrive.model.File;
import com.supinfo.supdrive.model.Folder;
import com.supinfo.supdrive.model.Offre;
import com.supinfo.supdrive.model.User;
import com.supinfo.supdrive.repository.FilesRepository;
import com.supinfo.supdrive.repository.OffreRepository;
import com.supinfo.supdrive.repository.UserRepository;
import com.supinfo.supdrive.security.CurrentUser;
import com.supinfo.supdrive.security.UserPrincipal;
import com.supinfo.supdrive.service.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FilesController {

    @Autowired
    FilesRepository filesRepository;

    @Autowired
    OffreRepository offreRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FilesService filesService;


    @Autowired
    public FilesController(){
    }

    // Create a file ( by name & folder's UUID )
    @PostMapping({"/files/upload/{uuid}", "/files/upload"})
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file,
                                                 @PathVariable(value = "uuid", required = false) UUID parentUuidFolder,
                                                 @CurrentUser UserPrincipal currentUser) {

        User user = getUser(currentUser);
        Offre offre = offreRepository.findByName(user.getOffre().getName())
                .orElseThrow(() -> new ResourceNotFoundException("Offre", "id", user.getOffre().getId()));

        if (filesRepository.sumByUserId(user.getId()) == null){
            user.setCurrentDataSize(0L);
        }else {user.setCurrentDataSize(filesRepository.sumByUserId(user.getId()));}

        if (user.getCurrentDataSize() + file.getSize() > offre.getMaxSize()){
            return ResponseEntity.status(HttpStatus.INSUFFICIENT_STORAGE).body("You did not have enough storage");
        }

        File uploadedFile = filesService.uploadFile(file, parentUuidFolder, user);
        return ResponseEntity.ok().body(uploadedFile);
    }

    // Update a File
    @PutMapping("/files/{uuid}")
    public ResponseEntity<?> updateFile(@PathVariable(value = "uuid") UUID fileUuid,
                               @Valid @RequestBody File fileUpdate,
                               @CurrentUser UserPrincipal currentUser) {

        User user = getUser(currentUser);
        File file = filesRepository.findByUuidAndUser(fileUuid, user);
        if (fileUpdate.getName() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Filename must not be null");
        }
        file.setName(fileUpdate.getName());
        File updateFile = filesRepository.save(file);
        return ResponseEntity.ok().body(updateFile);
    }

    // Move a file
    @PutMapping("/files/move/{uuid}")
    public ResponseEntity<?> moveFile(@PathVariable(value = "uuid") UUID fileUuid,
                           @Valid @RequestBody Folder newFolder,
                           @CurrentUser UserPrincipal currentUser) {

        User user = getUser(currentUser);
        if (newFolder.getUuid() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have to specify a folder UUID");
        }
        File finalUpdateFile = filesService.moveFile(fileUuid, newFolder, user);
        return ResponseEntity.ok().body(finalUpdateFile);
    }

    // Share a file
    @PutMapping("/files/share/{uuid}")
    public ResponseEntity<?> shareFile(@PathVariable(value = "uuid") UUID fileUuid,
                         @Valid @RequestBody File newfile,
                         @CurrentUser UserPrincipal currentUser) {

        User user = new User();
        user.setId(currentUser.getId());
        File file = filesRepository.findByUuidAndUser(fileUuid, user);
        if (file == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File with uuid " + fileUuid + " not found");
        }else {
            file.setShared(newfile.getShared());
            File finalUpdateFile = filesRepository.save(file);
            return ResponseEntity.ok().body(finalUpdateFile);
        }
    }

    // Delete a file
    @DeleteMapping("/files/{uuid}")
    public ResponseEntity deleteFile(@PathVariable(value = "uuid") UUID fileUuid,
                           @CurrentUser UserPrincipal currentUser){

        User user = getUser(currentUser);
        filesService.deleteFile(fileUuid, user);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    private User getUser(UserPrincipal currentUser){

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));
        return user;
    }

}