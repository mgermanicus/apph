package com.viseo.apph.domain;

import javax.persistence.*;

@Entity
@Table(name = "role")
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true)
    ERole name;

    public Role() {
        super();
    }

    public Role(ERole name) {
        this.name = name;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }
}