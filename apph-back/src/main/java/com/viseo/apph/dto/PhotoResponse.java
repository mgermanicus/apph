package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class PhotoResponse implements IResponseDTO {

    @JsonProperty("title")
    String title;
    @JsonProperty("description")
    String description;
    @JsonProperty("creationDate")
    Date creationDate;
    @JsonProperty("shootingDate")
    Date shootingDate;
    @JsonProperty("size")
    float size;
    @JsonProperty("tags")
    String tags;
    @JsonProperty("url")
    String url;
    @JsonProperty("data")
    byte[] data;
    @JsonProperty("extension")
    String extension;

    public PhotoResponse() {
    }

    public String getTitle() {
        return title;
    }

    public PhotoResponse setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PhotoResponse setDescription(String description) {
        this.description = description;
        return this;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public PhotoResponse setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public Date getShootingDate() {
        return shootingDate;
    }

    public PhotoResponse setShootingDate(Date shootingDate) {
        this.shootingDate = shootingDate;
        return this;
    }

    public float getSize() {
        return size;
    }

    public PhotoResponse setSize(float size) {
        this.size = size;
        return this;
    }

    public String getTags() {
        return tags;
    }

    public PhotoResponse setTags(String tags) {
        this.tags = tags;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public PhotoResponse setUrl(String url) {
        this.url = url;
        return this;
    }

    public byte[] getData() {
        return data;
    }

    public PhotoResponse setData(byte[] data) {
        this.data = data;
        return this;
    }

    public String getExtension() {
        return extension;
    }

    public PhotoResponse setExtension(String extension) {
        this.extension = extension;
        return this;
    }
}
