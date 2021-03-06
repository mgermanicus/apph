package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.viseo.apph.domain.Location;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.Position;
import com.viseo.apph.domain.Tag;

import java.time.LocalDate;
import java.util.Set;

public class PhotoResponse implements IResponseDto {
    @JsonProperty("id")
    long id;
    @JsonProperty("title")
    String title;
    @JsonProperty("description")
    String description;
    @JsonProperty("creationDate")
    LocalDate creationDate;
    @JsonProperty("modificationDate")
    LocalDate modificationDate;
    @JsonProperty("shootingDate")
    LocalDate shootingDate;
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
    @JsonProperty("location")
    Location location;

    public PhotoResponse() {
    }

    public PhotoResponse(Photo photo) {
        this.id = photo.getId();
        this.title = photo.getTitle();
        this.creationDate = photo.getCreationDate();
        this.modificationDate = photo.getModificationDate();
        this.size = photo.getSize();
        this.tags = photo.getTags();
        this.description = photo.getDescription();
        this.shootingDate = photo.getShootingDate();
        this.format = photo.getFormat();
        Position position = new Position().setLat(photo.getLat()).setLng(photo.getLng());
        this.location = new Location().setAddress(photo.getAddress()).setPosition(position);
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

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public PhotoResponse setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public LocalDate getModificationDate() {
        return modificationDate;
    }

    public PhotoResponse setModificationDate(LocalDate modificationDate) {
        this.modificationDate = modificationDate;
        return this;
    }

    public LocalDate getShootingDate() {
        return shootingDate;
    }

    public PhotoResponse setShootingDate(LocalDate shootingDate) {
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

    public Location getLocation() {
        return location;
    }

    public PhotoResponse setLocation(Location location) {
        this.location = location;
        return this;
    }
}
