package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenRequest {
    @JsonProperty("token")
    String token;
    public String getToken(){
        return token;
    }
}
