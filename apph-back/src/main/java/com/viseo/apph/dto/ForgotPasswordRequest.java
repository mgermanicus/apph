package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ForgotPasswordRequest {
    @JsonProperty("login")
    String login;
    @JsonProperty("language")
    String language;
    public String getLogin(){
        return login;
    }

    public String getLanguage(){
        return language;
    }

    public ForgotPasswordRequest setLogin(String login) {
        this.login = login;
        return this;
    }
    public ForgotPasswordRequest setLanguage(String language) {
        this.language = language;
        return this;
    }
}
