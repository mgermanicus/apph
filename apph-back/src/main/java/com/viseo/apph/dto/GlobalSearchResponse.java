package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class GlobalSearchResponse implements IResponseDto {
    @JsonProperty("photoList")
    List<PhotoResponse> photoList;

    @JsonProperty("total")
    long total;

    public GlobalSearchResponse() {
        this.photoList = new ArrayList<>();
    }

    public GlobalSearchResponse addPhoto(PhotoResponse photo) {
        this.photoList.add(photo);
        return this;
    }

    public List<PhotoResponse> getPhotoList() {
        return this.photoList;
    }

    public long getTotal() {
        return total;
    }

    public GlobalSearchResponse setTotal(long total) {
        this.total = total;
        return this;
    }
}
