package com.tech.travel.repository;

import com.tech.travel.models.Location;
import com.tech.travel.models.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Integer> {
    @Query("SELECT t, l "+
            "FROM Translation AS t \n" +
            "JOIN Location AS l\n" +
            "ON t.location = l.id\n" +
            "WHERE t.language=:language"
    )
    List<Translation> findAllLocationsByTLanguage(String language);

    @Query("SELECT t, l "+
            "FROM Translation AS t \n" +
            "JOIN Location AS l\n" +
            "ON t.location = l.id\n" +
            "WHERE l.type=:type AND l.code=:code AND t.language=:language"
    )
    Translation findLocationByLanguageTypeAndCode(String language, Location.LocationType type, String code);

}
