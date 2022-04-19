package com.viseo.apph.domain;

import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name="users")
public class User extends BaseEntity {
    private static final long serialVersionUID = 1L;
    String login;
    String password;

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


}