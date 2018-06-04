package com.supinfo.supdrive.repository;

import com.supinfo.supdrive.model.File;
import com.supinfo.supdrive.model.Folder;
import com.supinfo.supdrive.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    Optional<Folder> findById(Long folderId);

    List<Folder> findByIdIn(List<Long> folderIds);

    List<Folder> findByIdIn(List<Long> folderIds, Sort sort);

    Folder findByNameAndIsDefaultDirectoryAndUserId(String name, Boolean isDefaultDirectory, Long userId);

    Folder findByUuidAndUser(UUID uuid, User user);

    Folder findByUuidAndShared(UUID uuid, Boolean shared);

    @Query(value = "select * from folders where name like (:queryName) and user_id = (:userId)", nativeQuery = true)
    List<Folder> findByName(@Param("queryName") String queryName, @Param("userId") long userId);

    @Query(value = "select * from folders where shared = true", nativeQuery = true)
    List<Folder> getAllShareFolder();

    @Transactional
    Integer deleteByIdAndUser(Long folderId, User user);

}