package com.supinfo.supdrive.controller;


import com.supinfo.supdrive.exception.ResourceNotFoundException;
import com.supinfo.supdrive.model.Folder;
import com.supinfo.supdrive.model.Owner;
import com.supinfo.supdrive.repository.FolderRepository;
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

    // Get File by owner
   // @GetMapping("/folder/{folder}")
    //public List<Folder> getAllDataByOwner(@PathVariable(value = "owner") Owner owner){
      //  return folderRepository.findByOwner(owner);
   // }

    // Create a new Files
    @PostMapping("/folder")
    public Folder createFolder(@Valid @RequestBody Folder folder) {
        folder.setUuid(getUuid());
        return folderRepository.save(folder);
    }

    // Get a Single Files
    @GetMapping("/folder/{id}")
    public Folder getFolderById(@PathVariable(value = "id") Long folderId) {
        return folderRepository.findById(folderId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder", "id", folderId));
    }

    // Update a Files
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