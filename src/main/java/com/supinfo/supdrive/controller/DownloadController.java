package com.supinfo.supdrive.controller;

import com.supinfo.supdrive.exception.ResourceNotFoundException;
import com.supinfo.supdrive.model.File;
import com.supinfo.supdrive.model.User;
import com.supinfo.supdrive.repository.FilesRepository;
import com.supinfo.supdrive.repository.FolderRepository;
import com.supinfo.supdrive.repository.UserRepository;
import com.supinfo.supdrive.security.CurrentUser;
import com.supinfo.supdrive.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;


@Controller
@RequestMapping("/api/download")
public class DownloadController {

    @Autowired
    FolderController folderController;

    @Autowired
    FilesRepository filesRepository;

    @Autowired
    UserRepository userRepository;

    @Value("${storage.location}")
    private String LOCATION;

    @GetMapping("/files/{uuid}")
    public HttpEntity<byte[]> getFile(@PathVariable(value = "uuid") UUID fileUuid,
                                      @CurrentUser UserPrincipal currentUser){

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        File file = filesRepository.findByUuidAndUser(fileUuid, user);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Disposition", "attachment; filename=" + file.getName() + file.getExtention());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        FileSystemResource fileSystemResource = new FileSystemResource(LOCATION + "/" + file.getUuid());

        HttpEntity httpEntity = new HttpEntity(fileSystemResource, headers);

        return httpEntity;
    }

}
