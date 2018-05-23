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
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.IOUtils;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
    private String location;

    @Autowired
    public FilesController(StorageService storageService) {
        this.storageService = storageService;
    }

    // Create a file ( by name & folder's UUID )
    @PostMapping("/files/upload/{uuid}")
    public ResponseEntity<File> handleFileUpload(@RequestParam("file") MultipartFile file,
                                                 @PathVariable(value = "uuid", required = false) UUID parentUuidFolder,
                                                 @CurrentUser UserPrincipal currentUser) {

        User user = new User();
        user.setId(currentUser.getId());

        Folder parentFolder = folderRepository.findByUuidAndUser(parentUuidFolder, user);
        File fileToUpload = new File();
        fileToUpload.setName(file.getOriginalFilename());
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
            Path path = Paths.get(location + "/" + fileToUpload.getUuid());
            Files.write(path, bytes);
            path = Paths.get(location + "/" + fileToUpload.getName());
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File toReturn = filesRepository.save(fileToUpload);
        return ResponseEntity.ok().body(toReturn);
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

}