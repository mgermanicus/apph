package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserResponse implements IResponseDto {

    @JsonProperty("login")
    String login;
    @JsonProperty("firstname")
    String firstname;
    @JsonProperty("lastname")
    String lastname;

    public UserResponse() {
        super();
    }

    public String getLogin() {
        return login;
    }

    public UserResponse setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getFirstname() {
        return firstname;
    }

    public UserResponse setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public String getLastname() {
        return lastname;
    }

    public UserResponse setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }
}
