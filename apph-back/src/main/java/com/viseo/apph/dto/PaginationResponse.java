package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class PaginationResponse implements IResponseDto {
    @JsonProperty("photoList")
    List<PhotoResponse> photoList;

    @JsonProperty("totalSize")
    int totalSize;

    public PaginationResponse() {
        this.photoList = new ArrayList<>();
    }

    public int getTotalSize() {
        return totalSize;
    }

    public PaginationResponse setTotalSize(int totalSize) {
        this.totalSize = totalSize;
        return this;
    }

    public PaginationResponse addPhoto(PhotoResponse photo) {
        this.photoList.add(photo);
        return this;
    }

    public List<PhotoResponse> getPhotoList() {
        return this.photoList;
    }
}
