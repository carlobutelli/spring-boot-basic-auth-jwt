package com.tech.travel.api.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ListLocationResponse {

    private List<LocationResponse> data;

    public ListLocationResponse(List<LocationResponse> data) {
        this.data = data;
    }
}