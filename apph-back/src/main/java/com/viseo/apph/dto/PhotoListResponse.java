package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class PhotoListResponse implements IResponseDto {
    @JsonProperty("photoList")
    List<PhotoResponse> photoList;

    @JsonProperty("total")
    long total;

    public PhotoListResponse() {
        this.photoList = new ArrayList<>();
    }

    public PhotoListResponse addPhoto(PhotoResponse photo) {
        this.photoList.add(photo);
        return this;
    }

    public List<PhotoResponse> getPhotoList() {
        return this.photoList;
    }

    public long getTotal() {
        return total;
    }

    public PhotoListResponse setTotal(long total) {
        this.total = total;
        return this;
    }
}
