package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class UserListResponse implements IResponseDto {
    @JsonProperty("userList")
    List<UserResponse> userList;

    public UserListResponse() {
        this.userList = new ArrayList<>();
    }

    public List<UserResponse> getUserList() {
        return userList;
    }

    public UserListResponse setUserList(List<UserResponse> userList) {
        this.userList = userList;
        return this;
    }
}
