package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageResponse implements ResponseDTO{
    @JsonProperty("message")
    String message;

    public MessageResponse(String message) {
        this.message = message;
    }
}
