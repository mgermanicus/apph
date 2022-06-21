package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class PhotosRequest {
    @JsonProperty("ids")
    long[] ids;
    @JsonProperty("folderId")
    long folderId;
    @JsonValue
    String shootingDate;
    @JsonValue
    String tags;


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

    public void setShootingDate(String shootingDate) {
        this.shootingDate = shootingDate;
    }

    public String getTags() {
        return tags;
    }

    public PhotosRequest setTags(String tags) {
        this.tags = tags;
        return this;
    }
}
