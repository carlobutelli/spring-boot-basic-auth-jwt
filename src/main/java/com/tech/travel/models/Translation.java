package com.tech.travel.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Translation", indexes = { @Index(name = "fki_translation_location", columnList = "location") })
public class Translation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "language")
    private String language;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "location")
    private Integer location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location", insertable = false, updatable = false)
    private Location fkLocation;

    public Translation(String language, String name, String description, Integer location) {
        setLanguage(language);
        setName(name);
        setDescription(description);
        setLocation(location);
    }

    @JsonIgnore
    public void setId(Integer id) {
        this.id = id;
    }
}
