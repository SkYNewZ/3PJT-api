package com.supinfo.supdrive.repository;

import com.supinfo.supdrive.model.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {

    Optional<Offre> findByName (String name);

    @Query(value = "select * from offres", nativeQuery = true)
    List <Offre> selectAll();

}
