package com.viseo.apph.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "photos")
public class Photo extends BaseEntity {

    String name;

    String extension;

    public Photo() {
        super();
    }

    public String getName() {
        return name;
    }

    public Photo setName(String name) {
        this.name = name;
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
