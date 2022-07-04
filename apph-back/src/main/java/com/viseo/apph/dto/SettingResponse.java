package com.viseo.apph.dto;

import com.viseo.apph.domain.Setting;

public class SettingResponse implements IResponseDto {
    long uploadSize;
    long downloadSize;
    String message;

    public SettingResponse() {
    }

    public SettingResponse(Setting setting) {
        this.uploadSize = setting.getUploadSize();
        this.downloadSize = setting.getDownloadSize();
    }

    public long getUploadSize() {
        return uploadSize;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public String getMessage() {
        return message;
    }

    public SettingResponse setMessage(String message) {
        this.message = message;
        return this;
    }
}
