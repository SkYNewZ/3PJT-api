package com.supinfo.supdrive.service;

import com.supinfo.supdrive.model.Folder;
import com.supinfo.supdrive.model.ResponseDto;
import com.supinfo.supdrive.model.User;
import com.supinfo.supdrive.repository.FilesRepository;
import com.supinfo.supdrive.repository.FolderRepository;
import com.supinfo.supdrive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FolderService {

    public FolderService() {
    }

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    FilesRepository filesRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FilesService filesService;

    @Value("${storage.location}")
    private String LOCATION;

    public Folder createFolder(Folder folder, UUID parentUuidFolder, User user){

        Folder createdFolder = new Folder();
        if (parentUuidFolder != null) {
            Folder parentFolder = folderRepository.findByUuidAndUser(parentUuidFolder, user);
            createdFolder.setFolder(parentFolder);
            createdFolder.setShared(parentFolder.getShared());

        }else {
            Folder parentFolder = folderRepository.findByNameAndIsDefaultDirectoryAndUserId("home", true, user.getId());
            createdFolder.setFolder(parentFolder);
            createdFolder.setShared(parentFolder.getShared());
        }
        createdFolder.setDefaultDirectory(false);
        createdFolder.setName(folder.getName());
        createdFolder.setUuid(getUuid());
        createdFolder.setUser(user);
        createdFolder.setMimeType("inode/directory");
        folderRepository.save(createdFolder);
        return createdFolder;
    }

    public ResponseDto getFolderContent(UUID folderUuid, User user){

        ResponseDto responseDto = new ResponseDto();

        if (folderUuid != null) {
            Folder folder = folderRepository.findByUuidAndUser(folderUuid, user);
            responseDto.setFiles(folder.getFiles());
            responseDto.setFolders(folder.getFolders());
            return responseDto;
        }else {
            Folder folder = folderRepository.findByNameAndIsDefaultDirectoryAndUserId("home", true, user.getId());
            responseDto.setFiles(folder.getFiles());
            responseDto.setFolders(folder.getFolders());
            return responseDto;
        }
    }

    public Folder moveFolder(UUID folderUuid, Folder newFolder, User user){

        Folder folder = folderRepository.findByUuidAndUser(folderUuid, user);
        if (newFolder.getUuid() == null) {

            folder.setFolder(folderRepository.findByNameAndIsDefaultDirectoryAndUserId("home", true, user.getId()));
            Folder finalUpdateFolder = folderRepository.save(folder);
            return finalUpdateFolder;

        } else {

            folder.setFolder(folderRepository.findByUuidAndUser(newFolder.getUuid(), user));
            Folder finalUpdateFolder = folderRepository.save(folder);
            return finalUpdateFolder;
        }
    }

    public Folder shareFolder(UUID folderUuid, Folder newFolder, User user){

        Folder folder = folderRepository.findByUuidAndUser(folderUuid, user);
        folder.setShared(newFolder.getShared());
        folder.getFiles().forEach(file -> {
            filesService.deleteFile(file.getUuid(), user);
        });
        folder.getFolders().forEach(folder1 -> {
            shareFolder(folder1.getUuid(), newFolder, user);
        });

        return folder;
    }

    public void deleteFolder(UUID folderUuid, User user){

        Folder folder = folderRepository.findByUuidAndUser(folderUuid, user);
        folderRepository.deleteByIdAndUser(folder.getId(), user);
    }

        private UUID getUuid(){
            UUID uuid = UUID.randomUUID();
            return uuid;
        }
}
