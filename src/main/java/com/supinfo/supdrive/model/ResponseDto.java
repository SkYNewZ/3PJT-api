package com.supinfo.supdrive.model;

import java.util.ArrayList;
import java.util.List;

public class ResponseDto {

    private List<File> files = new ArrayList<>();

    private List<Folder> folders = new ArrayList<>();

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }
}
