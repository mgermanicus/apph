package com.viseo.apph.dto;

public class FolderRequest {
    String name;
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
