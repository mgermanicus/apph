package com.viseo.apph.domain;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "photos")
public class Photo extends BaseEntity {

    String title;
    String description;
    Date creationDate;
    Date shootingDate;
    float size;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;


    @ElementCollection
    Set<String> tags;
    String format;

    public Photo() {
        super();
    }

    public User getUser() {
        return user;
    }

    public Photo setUser(User user) {
        this.user = user;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Photo setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Photo setDescription(String description) {
        this.description = description;
        return this;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Photo setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public Date getShootingDate() {
        return shootingDate;
    }

    public Photo setShootingDate(Date shootingDate) {
        this.shootingDate = shootingDate;
        return this;
    }

    public float getSize() {
        return size;
    }

    public Photo setSize(float size) {
        this.size = size;
        return this;
    }

    public Set<String> getTags() {
        return tags;
    }

    public Photo setTags(Set<String> tags) {
        this.tags = tags;
        return this;
    }

    public String getFormat() {
        return format;
    }

    public Photo setFormat(String format) {
        this.format = format;
        return this;
    }
}
