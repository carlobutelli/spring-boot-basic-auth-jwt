package com.tech.travel.api.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaseResponse {
    private String status;
    private String transactionId;
    private String message;
    private int statusCode;

    public BaseResponse(String status, String transactionId, String message, int statusCode) {
        this.setStatus(status);
        this.setTransactionId(transactionId);
        this.setMessage(message);
        this.setStatusCode(statusCode);
    }

}
