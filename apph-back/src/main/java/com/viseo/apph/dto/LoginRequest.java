package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {
    @JsonProperty("email")
    String login;
    @JsonProperty("password")
    String password;

    public String getPassword() {
        return password;
    }

    public LoginRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public LoginRequest setLogin(String login) {
        this.login = login;
        return this;
    }
}
