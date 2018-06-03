package com.supinfo.supdrive.controller;

import com.supinfo.supdrive.exception.ResourceNotFoundException;
import com.supinfo.supdrive.model.File;
import com.supinfo.supdrive.model.User;
import com.supinfo.supdrive.repository.FilesRepository;
import com.supinfo.supdrive.repository.FolderRepository;
import com.supinfo.supdrive.repository.UserRepository;
import com.supinfo.supdrive.security.CurrentUser;
import com.supinfo.supdrive.security.UserPrincipal;
import com.supinfo.supdrive.service.DownloadService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/api/download")
public class DownloadController {

    @Autowired
    FolderController folderController;

    @Autowired
    FilesRepository filesRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DownloadService downloadService;

    @Value("${storage.location}")
    private String LOCATION;

    @GetMapping("/files/{uuid}")
    public HttpEntity<byte[]> getFile(@PathVariable(value = "uuid") UUID fileUuid,
                                      @CurrentUser UserPrincipal currentUser){

        User user = getUser(currentUser);
        HttpEntity httpEntity = downloadService.downloadFile(fileUuid, user);
        return httpEntity;
    }

    @GetMapping(value = "/zip/files/{uuid}", produces="application/zip" )
    public HttpEntity<byte[]> zipFiles(@PathVariable(value = "uuid") UUID fileUuid,
                                       @CurrentUser UserPrincipal currentUser) throws IOException {

        User user = getUser(currentUser);
        HttpEntity httpEntity = downloadService.downloadZipFile(fileUuid, user);
        return httpEntity;
    }
    
    private User getUser(UserPrincipal currentUser){

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));
        return user;
    }

}
