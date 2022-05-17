package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhotosRequest {
    @JsonProperty("ids")
    long[] ids;

    public long[] getIds() {
        return ids;
    }

    public PhotosRequest setIds(long[] ids) {
        this.ids = ids;
        return this;
    }
}
