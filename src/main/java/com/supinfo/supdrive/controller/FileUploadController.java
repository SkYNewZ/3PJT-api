package com.supinfo.supdrive.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.supinfo.supdrive.exception.StorageFileNotFoundException;
import com.supinfo.supdrive.model.File;
import com.supinfo.supdrive.model.Folder;
import com.supinfo.supdrive.model.User;
import com.supinfo.supdrive.repository.FilesRepository;
import com.supinfo.supdrive.repository.FolderRepository;
import com.supinfo.supdrive.security.CurrentUser;
import com.supinfo.supdrive.security.UserPrincipal;
import com.supinfo.supdrive.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@Controller
@RequestMapping("/api")
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @Autowired
    FilesRepository filesRepository;

    @Autowired
    FolderRepository folderRepository;

    @Value("${storage.location}")
    private String location;

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/files/upload")
    public ResponseEntity<File> handleFileUpload(@RequestParam("file") MultipartFile file,
                                 @CurrentUser UserPrincipal currentUser, @RequestParam(value = "folder", required = false) Folder folder) {

        User user = new User();
        File fileToUpload = new File();
        fileToUpload.setName(file.getOriginalFilename());
        fileToUpload.setUuid(getUuid());
        fileToUpload.setMimeType(file.getContentType());
        fileToUpload.setExtention(getNameExtention(file.getOriginalFilename()));
        user.setId(currentUser.getId());
        if (folder == null){
            folder = folderRepository.findByNameAndIsDefaultDirectoryAndUserId("home", true, user.getId());
        }

        fileToUpload.setFolder(folder);
        fileToUpload.setUser(user);
        storageService.store(file);

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(location + "/" +fileToUpload.getUuid());
            Files.write(path, bytes);
            Files.delete(path);
        }
         catch (IOException e) {
        e.printStackTrace();
    }
        File toReturn = filesRepository.save(fileToUpload);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + toReturn.getName() + "\"").body(toReturn);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    private UUID getUuid(){
        return UUID.randomUUID();
    }

    private String getNameExtention(String name){

        String Result;
        String pattern = "\\.(\\w+)$";
        Pattern c = Pattern.compile(pattern);
        Matcher m = c.matcher(name);
        if (m.find()){
            Result = m.group();
        }else Result = name;

        return Result;
    }

}