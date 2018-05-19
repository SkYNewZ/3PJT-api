package com.supinfo.supdrive.controller;


import com.supinfo.supdrive.exception.ResourceNotFoundException;
import com.supinfo.supdrive.model.File;
import com.supinfo.supdrive.model.Folder;
import com.supinfo.supdrive.repository.FilesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FilesController {

    @Autowired
    FilesController filesController;

    @Autowired
    FilesRepository filesRepository;

    @Value("${storage.location}")
    private String location;

    // Get File by owner
    @GetMapping("/folder/{folder}/files")
    public List<File> getAllFilesAtOwner(@PathVariable(value = "folder") Integer folderId){
        return filesRepository.findByFolderId(folderId);
    }

    // Create a new Files
    @PostMapping("/files")
    public File createFile(@Valid @RequestBody File files, @RequestBody Folder folder) {
        files.setUuid(getUuid());
        files.setFolder(folder);
        return filesRepository.save(files);
    }

    // Get a Single Files
    @GetMapping("/files/{id}")
    public File getFileById(@PathVariable(value = "id") Long fileID) {
        return filesRepository.findById(fileID)
                .orElseThrow(() -> new ResourceNotFoundException("Files", "id", fileID));
    }

    // Update a Files
    @PutMapping("/files/{id}")
    public File updateFile(@PathVariable(value = "id") Long fileID,
                           @Valid @RequestBody File filesDetails) {

        File files = filesRepository.findById(fileID)
                .orElseThrow(() -> new ResourceNotFoundException("Files", "id", fileID));

        files.setName(filesDetails.getName());

        return filesRepository.save(files);
    }

    // Delete a Files
    @DeleteMapping("/files/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable(value = "id") Long fileID) {
        File files = filesRepository.findById(fileID)
                .orElseThrow(() -> new ResourceNotFoundException("Files", "id", fileID));

        filesRepository.delete(files);

        return ResponseEntity.ok().build();
    }

    private UUID getUuid(){
        return UUID.randomUUID();
    }

}