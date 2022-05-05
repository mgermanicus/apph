package com.viseo.apph.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "parentFolderId" }) })
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
}
