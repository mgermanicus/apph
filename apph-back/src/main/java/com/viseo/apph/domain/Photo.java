package com.viseo.apph.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "photos")
public class Photo extends BaseEntity {

    String title;
    long idUser;
    String description;
    Date creationDate;
    Date shootingDate;
    float size;
    String tags;

    String extension;

    public Photo() {
        super();
    }

    public long getIdUser() {
        return idUser;
    }

    public Photo setIdUser(long idUser) {
        this.idUser = idUser;
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

    public String getTags() {
        return tags;
    }

    public Photo setTags(String tags) {
        this.tags = tags;
        return this;
    }

    public String getExtension() {
        return extension;
    }

    public Photo setExtension(String extension) {
        this.extension = extension;
        return this;
    }
}
