package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDeleteRequest {
    @JsonProperty("email")
    String email;

    public String getEmail() {
        return email;
    }

    public UserDeleteRequest setEmail(String email) {
        this.email = email;
        return this;
    }

}
