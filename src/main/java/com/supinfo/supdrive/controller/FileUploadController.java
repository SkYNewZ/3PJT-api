package com.supinfo.supdrive.controller;

import java.io.IOException;
import java.net.URLConnection;
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
import com.supinfo.supdrive.repository.FilesRepository;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public File handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        File fileToUpload = new File();
        Folder folder = new Folder();
        fileToUpload.setName(file.getOriginalFilename());
        fileToUpload.setUuid(getUuid());
        fileToUpload.setMimeType(URLConnection.guessContentTypeFromName(file.getName()));
        fileToUpload.setExtention(getNameExtention(file.getOriginalFilename()));
        fileToUpload.setFolder(folder);
        storageService.store(file);

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(location + fileToUpload.getUuid());
            Files.write(path, bytes);
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded " + file.getOriginalFilename() + "!");
        }
         catch (IOException e) {
        e.printStackTrace();
    }

        return filesRepository.save(fileToUpload);
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