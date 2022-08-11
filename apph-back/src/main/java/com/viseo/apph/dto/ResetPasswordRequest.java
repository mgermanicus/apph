package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResetPasswordRequest {
    @JsonProperty("password")
    String password;
    @JsonProperty("token")
    String token;

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public ResetPasswordRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public ResetPasswordRequest setToken(String token) {
        this.token = token;
        return this;
    }
}
