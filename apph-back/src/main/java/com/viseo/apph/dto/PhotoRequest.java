package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.multipart.MultipartFile;

public class PhotoRequest {
    @JsonProperty("id")
    long id;

    String title;
    MultipartFile file;
    Set<Tag> tags;

    public long getId() {
        return id;
    }

    public PhotoRequest setId(long id) {
        this.id = id;
        return this;
    }

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

    public Set<Tag> getTags() {
        return tags;
    }
}
