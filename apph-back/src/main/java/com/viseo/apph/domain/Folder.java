package com.viseo.apph.domain;

import javax.persistence.*;

@Entity
public class Folder extends BaseEntity {
    String name;

    Long parentFolderId;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

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
        return this.user;
    }

    public Folder setUser(User user) {
        if (user != this.user) {
            if (this.user != null) {
                throw new IllegalArgumentException("This folder is already owned by an user.");
            }
            this.user = user;
            if (this.user != null) {
                this.user.folders.add(this);
            }
        }
        return this;
    }
}
