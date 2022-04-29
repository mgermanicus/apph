package com.viseo.apph.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @Column(unique=true)
    String login;
    String password;
    String firstname;
    String lastname;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Folder> folders = new ArrayList<>();

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
}