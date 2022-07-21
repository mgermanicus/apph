package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.viseo.apph.domain.Location;

public class PhotosRequest {
    @JsonProperty("ids")
    long[] ids;
    @JsonProperty("folderId")
    long folderId;
    @JsonValue
    String shootingDate;
    @JsonValue
    String tags;
    @JsonValue
    String location;


    public long[] getIds() {
        return ids;
    }

    public PhotosRequest setIds(long[] ids) {
        this.ids = ids;
        return this;
    }

    public long getFolderId() {
        return folderId;
    }

    public PhotosRequest setFolderId(long folderId) {
        this.folderId = folderId;
        return this;
    }

    public String getShootingDate() {
        return shootingDate;
    }

    public PhotosRequest setShootingDate(String shootingDate) {
        this.shootingDate = shootingDate;
        return this;
    }

    public String getTags() {
        return tags;
    }

    public PhotosRequest setTags(String tags) {
        this.tags = tags;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public PhotosRequest setLocation(String location) {
        this.location = location;
        return this;
    }
}
