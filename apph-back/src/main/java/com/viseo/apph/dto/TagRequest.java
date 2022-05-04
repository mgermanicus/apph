package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TagRequest {
    @JsonProperty("name")
    String name;

    @JsonProperty("id")
    String id;
}
