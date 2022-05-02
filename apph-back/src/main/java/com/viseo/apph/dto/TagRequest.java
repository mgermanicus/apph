package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.viseo.apph.domain.User;

public class TagRequest {
    @JsonProperty("id")
    long id;

    @JsonProperty("name")
    String name;

    @JsonProperty("user")
    User user;

    public String getName() {
        return name;
    }

    public TagRequest setName(String name) {
        this.name = name;
        return this;
    }

    public User getUser() {
        return user;
    }

    public TagRequest setUser(User user) {
        this.user = user;
        return this;
    }
}
