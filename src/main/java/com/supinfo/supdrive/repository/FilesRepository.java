package com.supinfo.supdrive.repository;

import com.supinfo.supdrive.model.File;
import com.supinfo.supdrive.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface FilesRepository extends JpaRepository<File, Long> {

    File findByUuidAndUser (UUID uuid, User user);

    @Transactional
    Integer deleteByIdAndUser(Long fileId, User user);


}
