package com.viseo.apph.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name="tags", uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "user_id" }) })
public class Tag extends BaseEntity {
    String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
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
}
