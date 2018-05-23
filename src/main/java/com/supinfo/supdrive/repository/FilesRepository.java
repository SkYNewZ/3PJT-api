package com.supinfo.supdrive.repository;

import com.supinfo.supdrive.model.File;
import com.supinfo.supdrive.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FilesRepository extends JpaRepository<File, Long> {

    public List<File> findByFolderId(Integer folderId);

    File findByUuidAndUser (UUID uuid, User user);

    File findByUuid(UUID uuid);

}
