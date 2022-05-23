package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.GsonBuilder;

public class FilterRequest {
    @JsonValue
    FilterDto[] filterList;
    @JsonValue
    int pageSize;
    @JsonValue
    int page;

    public int getPageSize() {
        return pageSize;
    }

    public FilterRequest setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public int getPage() {
        return page;
    }

    public FilterRequest setPage(int page) {
        this.page = page;
        return this;
    }

    public FilterDto[] getFilters() {
        return filterList;
    }

    public FilterRequest setFilterList(FilterDto[] filterList) {
        this.filterList = filterList;
        return this;
    }
}

