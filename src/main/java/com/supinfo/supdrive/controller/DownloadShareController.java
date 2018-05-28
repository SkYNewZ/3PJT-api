package com.supinfo.supdrive.controller;

import com.supinfo.supdrive.model.File;
import com.supinfo.supdrive.repository.FilesRepository;
import com.supinfo.supdrive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("/share")
public class DownloadShareController {

    @Autowired
    FilesRepository filesRepository;

    @Value("${storage.location}")
    private String LOCATION;

    // download shared's files
    @GetMapping("/file/download/{uuid}")
    public HttpEntity<byte[]> getFile(@PathVariable(value = "uuid") UUID fileUuid) {

        // TODO: 26/05/18 return exception if file not exist
        File file = filesRepository.findByUuidAndShared(fileUuid, true);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Disposition", "attachment; filename=" + file.getName() + file.getExtention());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        FileSystemResource fileSystemResource = new FileSystemResource(LOCATION + "/" + file.getUuid());

        HttpEntity httpEntity = new HttpEntity(fileSystemResource, headers);

        return httpEntity;
    }

}
