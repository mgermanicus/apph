package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FolderRequest {
    @JsonProperty("name")
    String name;
    @JsonProperty("parentFolderId")
    Long parentFolderId;

    public String getName() {
        return name;
    }

    public FolderRequest setName(String name) {
        this.name = name;
        return this;
    }

    public Long getParentFolderId() {
        return parentFolderId;
    }

    public FolderRequest setParentFolderId(Long parentFolderId) {
        this.parentFolderId = parentFolderId;
        return this;
    }
}
