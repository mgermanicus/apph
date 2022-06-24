package com.viseo.apph.dto;

import com.viseo.apph.domain.Setting;

public class SettingResponse implements IResponseDto{
    long uploadSize;
    long downloadSize;
    String message;

    public SettingResponse(){}

    public SettingResponse(Setting setting) {
        System.out.println(setting.getUploadSize());
        System.out.println(setting.getDownloadSize());
        this.uploadSize = setting.getUploadSize();
        this.downloadSize = setting.getDownloadSize();
    }

    public long getUploadSize() {
        return uploadSize;
    }

    public SettingResponse setUploadSize(long uploadSize) {
        this.uploadSize = uploadSize;
        return this;
    }

    public long getDownloadSize() {
        return uploadSize;
    }

    public SettingResponse setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public SettingResponse setMessage(String message) {
        this.message = message;
        return this;
    }
}
