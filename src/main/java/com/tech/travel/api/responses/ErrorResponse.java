package com.tech.travel.api.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse{

    private BaseResponse meta;

    public ErrorResponse(BaseResponse response) {
        this.meta = response;
    }

}
