package com.tech.travel.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {
    @NotBlank
    @Size(max = 20)
    @JsonProperty("username")
    private String username;

    @NotBlank
    @Size(max = 120)
    @JsonProperty("password")
    private String password;

    private Boolean enabled;

    @JsonProperty("roles")
    private Set<String> roles;
}
