package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhotosRequest {
    @JsonProperty("ids")
    long[] ids;
    @JsonProperty("folderId")
    long folderId;
    @JsonProperty("titleZip")
    String titleZip;

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
}
