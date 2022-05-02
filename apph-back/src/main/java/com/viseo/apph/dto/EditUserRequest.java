package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EditUserRequest {

    @JsonProperty("firstname")
    String firstname;

    @JsonProperty("lastname")
    String lastname;

    @JsonProperty("login")
    String login;

    @JsonProperty("password")
    String password;

    public String getFirstname() {
        return firstname;
    }

    public EditUserRequest setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public String getLastname() {
        return lastname;
    }

    public EditUserRequest setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public EditUserRequest setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public EditUserRequest setPassword(String password) {
        this.password = password;
        return this;
    }

}
