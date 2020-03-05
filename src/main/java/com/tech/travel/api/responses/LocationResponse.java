package com.tech.travel.api.responses;

import com.tech.travel.models.Location;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationResponse implements Serializable {
    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    @Enumerated(EnumType.STRING)
    private Location.LocationType type;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("description")
    private String description;

    private Integer parentCode;

    @Enumerated(EnumType.STRING)
    private Location.LocationType parentType;

    public LocationResponse(String code, String name, Location.LocationType type, Double longitude, Double latitude,
                            String description, Integer parentCode, Location.LocationType parentType) {
        setCode(code);
        setName(name);
        setType(type);
        setLongitude(longitude);
        setLatitude(latitude);
        setDescription(description);
        setParentCode(parentCode);
        setParentType(parentType);
    }


}
