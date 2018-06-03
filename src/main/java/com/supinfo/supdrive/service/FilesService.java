package com.supinfo.supdrive.service;

import com.supinfo.supdrive.controller.FilesController;
import com.supinfo.supdrive.model.File;
import com.supinfo.supdrive.model.Folder;
import com.supinfo.supdrive.model.User;
import com.supinfo.supdrive.repository.FilesRepository;
import com.supinfo.supdrive.repository.FolderRepository;
import com.supinfo.supdrive.repository.OffreRepository;
import com.supinfo.supdrive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FilesService {

    public FilesService(StorageService storageService) {
        this.storageService = storageService;
    }

    private final StorageService storageService;

    @Autowired
    FilesController filesController;

    @Autowired
    FilesRepository filesRepository;

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    OffreRepository offreRepository;

    @Autowired
    UserRepository userRepository;

    @Value("${storage.location}")
    private String LOCATION;

    public File uploadFile(MultipartFile file, UUID parentUuidFolder, User user){

        Folder parentFolder = folderRepository.findByUuidAndUser(parentUuidFolder, user);
        File fileToUpload = createFile(file, parentFolder, user);

        try {
            storageService.store(file);
            byte[] bytes = file.getBytes();
            Path path = Paths.get(LOCATION + "/" + fileToUpload.getUuid());
            Files.write(path, bytes);
            path = Paths.get(LOCATION + "/" + file.getOriginalFilename());
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File uploadedFile = filesRepository.save(fileToUpload);

        return uploadedFile;
    }

    public File moveFile(UUID fileUuid, Folder newFolder, User user){

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

    public void deleteFile(UUID fileUuid, User user){

        File file = filesRepository.findByUuidAndUser(fileUuid, user);
        filesRepository.deleteByIdAndUser(file.getId(), user);

        try {

            Path path = Paths.get(LOCATION + "/" + fileUuid);
            Files.delete(path);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File createFile(MultipartFile file, Folder parentFolder, User user){

        File fileToUpload = new File();
        fileToUpload.setName(getNameWithoutExtention(file.getOriginalFilename()));
        fileToUpload.setUuid(getUuid());
        fileToUpload.setMimeType(file.getContentType());
        fileToUpload.setExtention(getNameExtention(file.getOriginalFilename()));
        fileToUpload.setFolder(parentFolder);
        // check if parentFolder exist
        if (parentFolder == null) {
            parentFolder = folderRepository.findByNameAndIsDefaultDirectoryAndUserId("home", true, user.getId());
            fileToUpload.setFolder(parentFolder);
        }
        //check if parentFolder is shared
        parentFolder = folderRepository.findByUuidAndUser(parentFolder.getUuid(), user);
        if (parentFolder.getShared() == true){
            fileToUpload.setShared(true);
        }else {fileToUpload.setShared(false);}

        fileToUpload.setUser(user);
        fileToUpload.setCreatedBy(user.getUsername());
        fileToUpload.setUpdatedBy(user.getUsername());
        fileToUpload.setSize(file.getSize());


        return fileToUpload;
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
