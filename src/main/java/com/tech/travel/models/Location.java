package com.tech.travel.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "Location", indexes = { @Index(name = "fki_parent", columnList = "parent_id") })
@Entity
public class Location implements Serializable {

    public enum LocationType {
        country,
        city,
        airport
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Integer id;

    @Column(name = "code", unique = true)
    @NotNull
    private String code;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private LocationType type;

    @Column(name = "longitude", columnDefinition="Decimal(9,6)")
    private Double longitude;

    @Column(name = "latitude", columnDefinition="Decimal(9,6)")
    private Double latitude;

    @Nullable
    @Column(name = "parent_id")
    private Integer parentId;

    @Nullable
    @ManyToOne
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private Location loc;

    @JsonIgnore
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Translation> translations = new ArrayList<>();

    public Location(@NotNull String code, LocationType type, Double longitude, Double latitude, Integer parentId) {
        setCode(code);
        setType(type);
        setLongitude(longitude);
        setLatitude(latitude);
        setParentId(parentId);
    }

    @JsonIgnore
    public void setId(Integer id) {
        this.id = id;
    }

}
