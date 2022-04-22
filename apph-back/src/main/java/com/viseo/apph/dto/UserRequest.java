package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserRequest {
    @JsonProperty("id")
    long id;

    @JsonProperty("email")
    String login;

    @JsonProperty("password")
    String password;

    public String getLogin() {
        return login;
    }

    public UserRequest setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserRequest setPassword(String password) {
        this.password = password;
        return this;
    }
}
