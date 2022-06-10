package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SortDto {
    @JsonProperty("field")
    String field;
    @JsonProperty("sort")
    String sort;

    public String getField() {
        return field;
    }

    public SortDto setField(String field) {
        this.field = field;
        return this;
    }

    public String getSort() {
        return sort;
    }

    public SortDto setSort(String sort) {
        this.sort = sort;
        return this;
    }
}
