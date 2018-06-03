package com.supinfo.supdrive.service;

import com.supinfo.supdrive.controller.FolderController;
import com.supinfo.supdrive.model.File;
import com.supinfo.supdrive.model.Folder;
import com.supinfo.supdrive.model.User;
import com.supinfo.supdrive.repository.FilesRepository;
import com.supinfo.supdrive.repository.FolderRepository;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class DownloadService {

    public DownloadService() {
    }

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    FilesRepository filesRepository;



    @Value("${storage.location}")
    private String LOCATION;

    public HttpEntity downloadFile(UUID fileUuid, User user){

        File file = filesRepository.findByUuidAndUser(fileUuid, user);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Disposition", "attachment; filename=" + file.getName() + file.getExtention());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        FileSystemResource fileSystemResource = new FileSystemResource(LOCATION + "/" + file.getUuid());

        HttpEntity httpEntity = new HttpEntity(fileSystemResource, headers);

        return httpEntity;
    }

    public HttpEntity downloadZipFile(UUID fileUuid, User user) throws IOException {

        File file = filesRepository.findByUuidAndUser(fileUuid, user);
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-Disposition", "attachment; filename=" + file.getName() + ".zip");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

        zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
        FileInputStream fileInputStream = new FileInputStream(LOCATION + "/" + file.getUuid());
        IOUtils.copy(fileInputStream, zipOutputStream);

        fileInputStream.close();
        zipOutputStream.closeEntry();

        if (zipOutputStream != null) {
            zipOutputStream.finish();
            zipOutputStream.flush();
            IOUtils.closeQuietly(zipOutputStream);
        }
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);
        HttpEntity httpEntity = new HttpEntity(byteArrayOutputStream.toByteArray(), headers);
        return httpEntity;
    }
}
