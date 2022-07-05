package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SettingRequest {
    @JsonProperty("uploadSize")
    long uploadSize;
    @JsonProperty("downloadSize")
    long downloadSize;

    public long getUploadSize() {
        return uploadSize;
    }

    public SettingRequest setUploadSize(long uploadSize) {
        this.uploadSize = uploadSize;
        return this;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public SettingRequest setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
        return this;
    }
}
