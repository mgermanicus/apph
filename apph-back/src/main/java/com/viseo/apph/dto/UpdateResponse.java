package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateResponse implements IResponseDto {
    @JsonProperty("statusCode")
    long statusCode;
    String message;

    public UpdateResponse() {

    }

    public String getMessage() {
        return message;
    }

    public UpdateResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public UpdateResponse setStatusCode(long statusCode) {
        this.statusCode = statusCode;
        return this;
    }
}
