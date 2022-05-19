package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.viseo.apph.domain.Tag;

import java.util.Date;
import java.util.Set;

public class PhotoResponse implements IResponseDto {
    @JsonProperty("id")
    long id;
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
    Set<Tag> tags;
    @JsonProperty("url")
    String url;
    @JsonProperty("data")
    byte[] data;
    @JsonProperty("format")
    String format;

    public PhotoResponse() {

    }

    public long getId() {
        return id;
    }

    public PhotoResponse setId(long id) {
        this.id = id;
        return this;
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

    public Set<Tag> getTags() {
        return tags;
    }

    public PhotoResponse setTags(Set<Tag> tags) {
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

    public String getFormat() {
        return format;
    }

    public PhotoResponse setFormat(String format) {
        this.format = format;
        return this;
    }
}
