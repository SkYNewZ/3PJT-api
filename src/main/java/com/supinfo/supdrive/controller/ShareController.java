package com.supinfo.supdrive.controller;

import com.supinfo.supdrive.model.File;
import com.supinfo.supdrive.model.Folder;
import com.supinfo.supdrive.model.ResponseDto;
import com.supinfo.supdrive.model.User;
import com.supinfo.supdrive.repository.FilesRepository;
import com.supinfo.supdrive.repository.FolderRepository;
import com.supinfo.supdrive.security.CurrentUser;
import com.supinfo.supdrive.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/share")
public class ShareController {

    @Autowired
    FilesController filesController;

    @Autowired
    FilesRepository filesRepository;

    @Autowired
    FolderRepository folderRepository;

    @Value("${storage.location}")
    private String LOCATION;

    // Get shared file informations
    @GetMapping("/file/{uuid}")
    public ResponseEntity<?> GetSharedFile(@PathVariable(value = "uuid", required = false) UUID uuidFile) {

        if (uuidFile == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This file is not shared !");
        }else{
            File file = filesRepository.findByUuidAndShared(uuidFile, true);
            return ResponseEntity.ok().body(file);
        }
    }

    // Get shared folder informations
    @GetMapping("/folder/{uuid}")
    public ResponseEntity<?> GetSharedFolder(@PathVariable(value = "uuid", required = false) UUID uuidFolder) {

        if (uuidFolder == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This folder is not shared !");
        }else{
            Folder folder = folderRepository.findByUuidAndShared(uuidFolder, true);
            return ResponseEntity.ok().body(folder);
        }
    }

    // Get shared folders
    @GetMapping("/folder/content/{uuid}")
    public ResponseEntity<?> getSharedFilesByFolder(@PathVariable(value = "uuid") UUID folderUuid) {

        ResponseDto responseDto = new ResponseDto();
        Folder folder = folderRepository.findByUuidAndShared(folderUuid, true);
        if (!folder.getShared()){
            List<File> files = filesRepository.findByFolderAndShared(folder, true);
            responseDto.setFiles(files);
            responseDto.setFolders(folder.getFolders());
            return ResponseEntity.ok().body(responseDto);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This folder is not shared");
        }
    }

    // get all shared file & folder
    @GetMapping("/all")
    public ResponseEntity<?> getAllShareData(){

        ResponseDto responseDto = new ResponseDto();
        responseDto.setFolders(folderRepository.getAllShareFolder());
        responseDto.setFiles(filesRepository.getAllShareFiles());

        return ResponseEntity.ok().body(responseDto);
    }
}
