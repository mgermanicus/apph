package com.viseo.apph.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "parentFolderId"})})
public class Folder extends BaseEntity {
    String name;

    Long parentFolderId;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    @OneToMany(mappedBy = "folder", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Photo> photos = new ArrayList<>();

    public Folder() {
        super();
    }

    public String getName() {
        return name;
    }

    public Folder setName(String name) {
        this.name = name;
        return this;
    }

    public Long getParentFolderId() {
        return parentFolderId;
    }

    public Folder setParentFolderId(Long id) {
        this.parentFolderId = id;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Folder setUser(User user) {
        assert this.user == null;
        if (user != null) {
            this.user = user;
            this.user.folders.add(this);
        }
        return this;
    }

    public List<Photo> getPhotos() {
        return this.photos;
    }
}
