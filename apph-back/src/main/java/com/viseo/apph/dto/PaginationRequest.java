package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaginationRequest {
    @JsonProperty("pageSize")
    int pageSize;

    @JsonProperty("page")
    int page;

    public int getPageSize() {
        return pageSize;
    }

    public PaginationRequest setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public int getPage() {
        return page;
    }

    public PaginationRequest setPage(int page) {
        this.page = page;
        return this;
    }
}
