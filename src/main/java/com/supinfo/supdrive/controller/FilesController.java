package com.supinfo.supdrive.controller;

import com.supinfo.supdrive.exception.StorageFileNotFoundException;
import com.supinfo.supdrive.model.File;
import com.supinfo.supdrive.model.Folder;
import com.supinfo.supdrive.model.User;
import com.supinfo.supdrive.repository.FilesRepository;
import com.supinfo.supdrive.repository.FolderRepository;
import com.supinfo.supdrive.security.CurrentUser;
import com.supinfo.supdrive.security.UserPrincipal;
import com.supinfo.supdrive.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.header.Header;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class FilesController {

    private final StorageService storageService;

    @Autowired
    FilesController filesController;

    @Autowired
    FilesRepository filesRepository;

    @Autowired
    FolderRepository folderRepository;

    @Value("${storage.location}")
    private String LOCATION;

    @Autowired
    public FilesController(StorageService storageService) {
        this.storageService = storageService;
    }

    // Create a file ( by name & folder's UUID )
    @PostMapping({"/files/upload/{uuid}", "/files/upload"})
    public ResponseEntity<File> handleFileUpload(@RequestParam("file") MultipartFile file,
                                                 @PathVariable(value = "uuid", required = false) UUID parentUuidFolder,
                                                 @CurrentUser UserPrincipal currentUser) {

        User user = new User();
        user.setId(currentUser.getId());

        Folder parentFolder = folderRepository.findByUuidAndUser(parentUuidFolder, user);
        File fileToUpload = new File();
        fileToUpload.setName(getNameWithoutExtention(file.getOriginalFilename()));
        fileToUpload.setUuid(getUuid());
        fileToUpload.setMimeType(file.getContentType());
        fileToUpload.setExtention(getNameExtention(file.getOriginalFilename()));
        fileToUpload.setFolder(parentFolder);
        fileToUpload.setUser(user);


        if (parentFolder == null) {
            parentFolder = folderRepository.findByNameAndIsDefaultDirectoryAndUserId("home", true, user.getId());
            fileToUpload.setFolder(parentFolder);
        }

        storageService.store(file);

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(LOCATION + "/" + fileToUpload.getUuid());
            Files.write(path, bytes);
            path = Paths.get(LOCATION + "/" + file.getOriginalFilename());
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File toReturn = filesRepository.save(fileToUpload);
        return ResponseEntity.ok().body(toReturn);
    }

    // Update a File
    @PutMapping("/files/{uuid}")
    public File updateFile(@PathVariable(value = "uuid") UUID fileUuid,
                               @Valid @RequestBody File fileUpdate,
                               @CurrentUser UserPrincipal currentUser) {

        User user = new User();
        user.setId(currentUser.getId());
        File file = filesRepository.findByUuidAndUser(fileUuid, user);

        file.setName(fileUpdate.getName());

        File updateFile = filesRepository.save(file);
        return updateFile;

    }

    // Move a file
    @PutMapping("/files/move/{uuid}")
    public File moveFile(@PathVariable(value = "uuid") UUID fileUuid,
                           @Valid @RequestBody Folder newFolder,
                           @CurrentUser UserPrincipal currentUser) {

        User user = new User();
        user.setId(currentUser.getId());
        File file = filesRepository.findByUuidAndUser(fileUuid, user);
        if (newFolder.getUuid() == null) {

            Folder updateFolder = folderRepository.findByNameAndIsDefaultDirectoryAndUserId("home", true, user.getId());
            file.setFolder(updateFolder);

        }else {

            Folder updateFolder = folderRepository.findByUuidAndUser(newFolder.getUuid(), user);
            file.setFolder(updateFolder);
        }

        File finalUpdateFile = filesRepository.save(file);
        return finalUpdateFile;
    }

    // Share a folder
    @PutMapping("/files/share/{uuid}")
    public File shareFile(@PathVariable(value = "uuid") UUID fileUuid,
                         @Valid @RequestBody File newfile,
                         @CurrentUser UserPrincipal currentUser) {

        User user = new User();
        user.setId(currentUser.getId());
        File file = filesRepository.findByUuidAndUser(fileUuid, user);
        file.setShared(newfile.getShared());
        File finalUpdateFile = filesRepository.save(file);
        return finalUpdateFile;

    }



    // Delete a file
    @DeleteMapping("/files/{uuid}")
    public ResponseEntity deleteFile(@PathVariable(value = "uuid") UUID fileUuid,
                           @CurrentUser UserPrincipal currentUser){

        User user = new User();
        user.setId(currentUser.getId());
        File file = filesRepository.findByUuidAndUser(fileUuid, user);

        try {

            Path path = Paths.get(LOCATION + "/" + file.getUuid());
            Files.delete(path);

        } catch (IOException e) {
            e.printStackTrace();
        }

        filesRepository.deleteByIdAndUser(file.getId(), user);
        return ResponseEntity.noContent().build();

    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    // Generate a UUID
    private UUID getUuid() {
        return UUID.randomUUID();
    }

    // get the .extention of a file
    private String getNameExtention(String name) {

        String Result;
        String pattern = "\\.(\\w+)$";
        Pattern c = Pattern.compile(pattern);
        Matcher m = c.matcher(name);
        if (m.find()) {
            Result = m.group();
        } else Result = name;

        return Result;
    }

    private String getNameWithoutExtention(String name){

        String Result;
        String pattern = "(.+?)(\\.[^.]*$|$)";
        Pattern c = Pattern.compile(pattern);
        Matcher m = c.matcher(name);
        if (m.find()){
            Result = m.group(1);
        }else Result = name;

        return Result;
    }

}