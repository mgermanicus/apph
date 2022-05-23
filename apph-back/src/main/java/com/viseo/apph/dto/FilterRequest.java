package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.web.multipart.MultipartFile;

public class FilterRequest {
    @JsonValue
    FilterDto[] filters;

    public FilterDto[] getFilters() {
        return filters;
    }

    public FilterRequest setFilters(FilterDto[] filters) {
        this.filters = filters;
        return this;
    }
}

