package com.tech.travel.repository;

import com.tech.travel.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    @Query("SELECT l "+
            "FROM Location AS l \n" +
            "JOIN Translation AS t\n" +
            "ON  l.id = t.location\n" +
            "WHERE t.language=:language"
    )
    List<Location> findAllLocationsByLanguage(String language);
}
