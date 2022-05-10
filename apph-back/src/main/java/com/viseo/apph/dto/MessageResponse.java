package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageResponse implements IResponseDto {
    @JsonProperty("message")
    String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
