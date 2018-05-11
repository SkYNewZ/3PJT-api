package com.supinfo.supdrive.repository;

import com.supinfo.supdrive.model.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilesRepository extends JpaRepository<Files, Long> {

    public List<Files> findByOwner(Integer owner);

}
