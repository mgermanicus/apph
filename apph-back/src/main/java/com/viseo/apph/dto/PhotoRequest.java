package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhotoRequest {
    @JsonProperty("id")
    long id;

    public long getId() {
        return id;
    }
}
