package com.viseo.apph.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="photos")
public class Photo extends BaseEntity{

    String name;

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
}
