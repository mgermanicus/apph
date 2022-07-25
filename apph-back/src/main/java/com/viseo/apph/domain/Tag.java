package com.viseo.apph.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tag", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "user_id"})})
public class Tag extends BaseEntity {

    @FullTextField(analyzer = "name")
    @KeywordField(name = "name_keyword", searchable = Searchable.NO, aggregable = Aggregable.YES)
    String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    User user;

    @ManyToMany(mappedBy = "tags")
    Set<Photo> photos = new HashSet<>();

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
