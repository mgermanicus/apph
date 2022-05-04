package com.viseo.apph.domain;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "photos")
public class Photo extends BaseEntity {

    String title;
    String description;
    Date creationDate;
    Date shootingDate;
    float size;
    String format;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "photo_tag",
            joinColumns = @JoinColumn(name = "photo_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    Set<Tag> tags = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

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

    public Set<Tag> getTags() {
        return tags;
    }

    public Photo setTags(Tag tag) {
        this.tags.add(tag);
        return this;
    }

    public Photo setTags(Set<Tag> tags) {
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
