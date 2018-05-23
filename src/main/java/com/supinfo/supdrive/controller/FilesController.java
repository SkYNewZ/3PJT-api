package com.supinfo.supdrive.controller;


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
import sun.misc.IOUtils;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

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

    @GetMapping("/files")
    public ResponseEntity<List<File>> getFilesByFolder(@CurrentUser UserPrincipal currentUser, @RequestParam(value = "folder", required = false) UUID folderUuid) {
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

    private UUID getUuid() {
        return UUID.randomUUID();
    }

}