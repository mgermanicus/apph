package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.viseo.apph.domain.Tag;
import org.springframework.web.multipart.MultipartFile;

public class PhotoRequest {
    @JsonProperty("id")
    long id;
    String title;
    MultipartFile file;
    @JsonValue
    String tags;

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

    public String getTags() {
        return tags;
    }

    public PhotoRequest setTags(String tags) {
        this.tags = tags;
        return this;
    }
}

