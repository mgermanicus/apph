package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class PhotoListResponse implements IResponseDTO {
    @JsonProperty("photoList")
    List<PhotoResponse> photoList;

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
}
