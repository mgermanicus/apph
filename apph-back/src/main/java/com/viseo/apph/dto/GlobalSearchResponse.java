package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GlobalSearchResponse implements IResponseDto {
    @JsonProperty("photoList")
    List<PhotoResponse> photoList;

    @JsonProperty("total")
    long total;

    @JsonProperty("facets")
    Map<String, Map<?, Long>> facets;

    public GlobalSearchResponse() {
        this.photoList = new ArrayList<>();
    }

    public GlobalSearchResponse addPhoto(PhotoResponse photo) {
        this.photoList.add(photo);
        return this;
    }

    public GlobalSearchResponse setTotal(long total) {
        this.total = total;
        return this;
    }

    public void setTagFacets(Map<String, Map<?, Long>> facets) {
        this.facets = facets;
    }
}
