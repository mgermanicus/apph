package com.viseo.apph.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserRequest {
    @JsonProperty("id")
    long id;

    @JsonProperty("email")
    String login;

    @JsonProperty("password")
    String password;
}
