package com.viseo.apph.dto;

import org.springframework.web.multipart.MultipartFile;

public class PhotoRequest {
    String title;

    MultipartFile file;

    public String getTitle() {
        return title;
    }

    public PhotoRequest setTitle(String title) {
        this.title = title;
        return this;
    }

    public MultipartFile getFile() {
        return file;
    }

    public PhotoRequest setFile(MultipartFile file) {
        this.file = file;
        return this;
    }
}
