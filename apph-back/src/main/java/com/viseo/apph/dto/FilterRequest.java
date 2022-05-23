package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.web.multipart.MultipartFile;

public class FilterRequest {
    @JsonValue
    String[] shootingDate;

    public String[] getShootingDate() {
        return shootingDate;
    }

    public FilterRequest setShootingDate(String[] shootingDate) {
        this.shootingDate = shootingDate;
        return this;
    }
}

