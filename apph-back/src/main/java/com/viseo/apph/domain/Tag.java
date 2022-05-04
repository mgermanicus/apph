package com.viseo.apph.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tags")
public class Tag extends BaseEntity {
    String name;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    public Tag() {
        super();
    }

    public String getName() {
        return name;
    }

    public Tag setName(String name) {
        this.name = name;
        return this;
    }

    public Tag setUser(User user) {
        assert this.user == null;
        if (user != null) {
            this.user = user;
            this.user.tags.add(this);
        }
        return this;
    }

    public User getUser() {
        return user;
    }
}
