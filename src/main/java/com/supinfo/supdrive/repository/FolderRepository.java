package com.supinfo.supdrive.repository;

import com.supinfo.supdrive.model.Folder;
import com.supinfo.supdrive.model.Owner;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    Optional<Folder> findById(Long folderId);

    List<Folder> findByIdIn(List<Long> folderIds);

    List<Folder> findByIdIn(List<Long> folderIds, Sort sort);

}