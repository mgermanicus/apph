package com.viseo.apph.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @Column(unique = true)
    String login;
    String password;
    String firstname;
    String lastname;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "users_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Folder> folders = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Photo> photos = new ArrayList<>();

    public User() {
        super();
    }

    public String getLogin() {
        return login;
    }

    public User setLogin(String login) {
        this.login = login;
        return this;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public User setRoles(Set<Role> roles) {
        this.roles = roles;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getFirstname() {
        return firstname;
    }

    public User setFirstname(String firstName) {
        this.firstname = firstName;
        return this;
    }

    public String getLastname() {
        return lastname;
    }

    public User setLastname(String lastName) {
        this.lastname = lastName;
        return this;
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public User addFolder(Folder folder) {
        assert folder != null;
        if (!this.folders.contains(folder)) {
            this.folders.add(folder);
            folder.user = this;
        }
        return this;
    }

    public User removeFolder(Folder folder) {
        assert folder != null;
        if (this.folders.contains(folder)) {
            this.folders.remove(folder);
            folder.user = null;
        }
        return this;
    }

    public User addPhoto(Photo photo) {
        assert photo != null;
        if (!this.photos.contains(photo)) {
            this.photos.add(photo);
            photo.setUser(this);
        }
        return this;
    }
}