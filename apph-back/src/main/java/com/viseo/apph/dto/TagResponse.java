package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TagResponse {
    @JsonProperty("value")
    String name;
    @JsonProperty("count")
    int count;

    public String getName() {
        return name;
    }

    public TagResponse setName(String name) {
        this.name = name;
        return this;
    }

    public int getCount() {
        return count;
    }

    public TagResponse setCount(int count) {
        this.count = count;
        return this;
    }
}
