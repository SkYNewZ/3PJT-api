package com.supinfo.supdrive.repository;

import com.supinfo.supdrive.model.File;
import com.supinfo.supdrive.model.Folder;
import com.supinfo.supdrive.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.UUID;

@Repository
public interface FilesRepository extends JpaRepository<File, Long> {

    File findByUuidAndUser (UUID uuid, User user);

    File findByUuidAndShared(UUID uuid, Boolean shared);

    List<File> findByFolderAndShared(Folder folder, Boolean shared);

    @Query(value = "select sum (f.size) as currentDataSize from files f where user_id = (:userId)", nativeQuery = true)
    Long sumByUserId(@Param("userId") Long userId);

    @Query(value = "select * from files where name ilike (:queryName) and user_id = (:userId)", nativeQuery = true)
    List<File> findByName(@Param("queryName") String queryName, @Param("userId") long userId);

    @Query(value = "select * from files where shared = true", nativeQuery = true)
    List<File> getAllShareFiles();

    @Transactional
    Integer deleteByIdAndUser(Long fileId, User user);

}
