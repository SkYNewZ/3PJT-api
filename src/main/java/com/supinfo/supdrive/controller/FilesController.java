package com.supinfo.supdrive.controller;


import com.supinfo.supdrive.exception.ResourceNotFoundException;
import com.supinfo.supdrive.model.Files;
import com.supinfo.supdrive.repository.FilesRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Get File by owner
    @GetMapping("/folder/{folder}/files")
    public List<Files> getAllFilesAtOwner(@PathVariable(value = "folder") Integer folderId){
        return filesRepository.findByFolderId(folderId);
    }

    // Create a new Files
    @PostMapping("/files")
    public Files createFile(@Valid @RequestBody Files files) {
        files.setUuid(getUuid());
        return filesRepository.save(files);
    }

    // Get a Single Files
    @GetMapping("/files/{id}")
    public Files getFileById(@PathVariable(value = "id") Long fileID) {
        return filesRepository.findById(fileID)
                .orElseThrow(() -> new ResourceNotFoundException("Files", "id", fileID));
    }

    // Update a Files
    @PutMapping("/files/{id}")
    public Files updateFile(@PathVariable(value = "id") Long fileID,
                           @Valid @RequestBody Files filesDetails) {

        Files files = filesRepository.findById(fileID)
                .orElseThrow(() -> new ResourceNotFoundException("Files", "id", fileID));

        files.setName(filesDetails.getName());

        return filesRepository.save(files);
    }

    // Delete a Files
    @DeleteMapping("/files/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable(value = "id") Long fileID) {
        Files files = filesRepository.findById(fileID)
                .orElseThrow(() -> new ResourceNotFoundException("Files", "id", fileID));

        filesRepository.delete(files);

        return ResponseEntity.ok().build();
    }

    private UUID getUuid(){
        return UUID.randomUUID();
    }

}