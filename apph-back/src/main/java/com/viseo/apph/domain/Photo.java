package com.viseo.apph.domain;

import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Indexed
@Entity
@Table(name = "photo")
public class Photo extends BaseEntity {

    @FullTextField(analyzer = "name")
    String title;

    @FullTextField(analyzer = "name")
    String description;

    LocalDate creationDate;

    LocalDate modificationDate;

    LocalDate shootingDate;

    @GenericField(searchable = Searchable.NO, aggregable = Aggregable.YES)
    float size;

    @FullTextField
    String format;

    @FullTextField(analyzer = "name")
    String address;

    Float lat;

    Float lng;

    String url;

    @ManyToMany
    @IndexedEmbedded
    @JoinTable(name = "photo_tag",
            joinColumns = @JoinColumn(name = "photo_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    Set<Tag> tags = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @IndexedEmbedded(includeEmbeddedObjectId = true)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @IndexedEmbedded
    Folder folder;

    public Photo() {
        super();
    }

    public User getUser() {
        return user;
    }

    public Photo setUser(User user) {
        this.user = user;
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

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public Photo setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public LocalDate getModificationDate() {
        return modificationDate;
    }

    public Photo setModificationDate(LocalDate modificationDate) {
        this.modificationDate = modificationDate;
        return this;
    }

    public LocalDate getShootingDate() {
        return shootingDate;
    }

    public Photo setShootingDate(LocalDate shootingDate) {
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

    public Set<Tag> getTags() {
        return tags;
    }

    public Photo setTags(Set<Tag> tags) {
        this.tags = tags;
        return this;
    }

    public Photo addTag(Tag tag) {
        this.tags.add(tag);
        return this;
    }

    public String getFormat() {
        return format;
    }

    public Photo setFormat(String format) {
        this.format = format;
        return this;
    }

    public Folder getFolder() {
        return this.folder;
    }

    public Photo setFolder(Folder folder) {
        if (this.folder != folder) {
            if (this.folder != null) {
                this.folder.photos.remove(this);
            }
            if (folder != null) {
                this.folder = folder;
                this.folder.photos.add(this);
            } else {
                this.folder = null;
            }
        }
        return this;
    }

    public Photo setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public String getAddress() {
        return this.address;
    }

    public Photo setAddress(String address) {
        this.address = address;
        return this;
    }

    public Float getLat() {
        return this.lat;
    }

    public Photo setLat(Float lat) {
        this.lat = lat;
        return this;
    }

    public Float getLng() {
        return this.lng;
    }

    public Photo setLng(Float lng) {
        this.lng = lng;
        return this;
    }


}
