package com.viseo.apph.security;

import com.viseo.apph.domain.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

public interface Utils {
    default User getUser(){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (User) new User()
                .setLogin(userDetails.getLogin())
                .setPassword(userDetails.getPassword())
                .setFirstname(userDetails.firstname)
                .setLastname(userDetails.lastname)
                .setId(userDetails.id);
    }


}
