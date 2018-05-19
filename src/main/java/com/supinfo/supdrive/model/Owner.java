package com.supinfo.supdrive.model;

import javax.persistence.*;

public class Owner {

    private Folder folderId;

    private File FileId;

    private User userId;

    public Folder getFolderId() {
        return folderId;
    }

    public void setFolderId(Folder folderId) {
        this.folderId = folderId;
    }

    public File getFileId() {
        return FileId;
    }

    public void setFileId(File fileId) {
        FileId = fileId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }
}
