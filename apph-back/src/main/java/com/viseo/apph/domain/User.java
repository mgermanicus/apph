package com.viseo.apph.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @Column(unique=true)
    String login;
    String password;
    String firstName;
    String lastName;

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

    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
}