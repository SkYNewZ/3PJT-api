package com.supinfo.supdrive.repository;

import com.supinfo.supdrive.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    public List<Folder> findByOwner(Integer owner);

}