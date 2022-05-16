package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.web.multipart.MultipartFile;

public class PhotoRequest {
    @JsonProperty("id")
    long id;
    @JsonProperty("ids")
    long[] ids;
    String title;
    String description;
    MultipartFile file;
    @JsonValue
    String tags;
    @JsonValue
    String shootingDate;

    public long[] getIds() {
        return ids;
    }

    public PhotoRequest setIds(long[] ids) {
        this.ids = ids;
        return this;
    }

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

    public String getDescription() {
        return description;
    }

    public PhotoRequest setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getShootingDate() {
        return shootingDate;
    }

    public PhotoRequest setShootingDate(String shootingDate) {
        this.shootingDate = shootingDate;
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

