package com.supinfo.supdrive.repository;

import com.supinfo.supdrive.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilesRepository extends JpaRepository<File, Long> {

    public List<File> findByFolderId(Integer folderId);

}
