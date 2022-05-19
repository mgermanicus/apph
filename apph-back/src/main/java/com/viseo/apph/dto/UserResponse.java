package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserResponse implements IResponseDto{

    @JsonProperty("login")
    String email;
    @JsonProperty("firstname")
    String firstname;
    @JsonProperty("lastname")
    String lastname;

    public UserResponse(){
        super();
    }

    public String getEmail() {
        return email;
    }

    public UserResponse setEmail(String email) {
        this.email = email;
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
