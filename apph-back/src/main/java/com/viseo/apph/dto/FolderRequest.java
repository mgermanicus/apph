package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FolderRequest {
    @JsonProperty("name")
    String name;
    @JsonProperty("parentFolderId")
    Long parentFolderId;
    @JsonProperty("folderIdToBeMoved")
    Long folderIdToBeMoved;
    @JsonProperty("destinationFolderId")
    Long destinationFolderId;
    @JsonProperty("id")
    Long id;

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

    public Long getFolderIdToBeMoved() {
        return folderIdToBeMoved;
    }

    public FolderRequest setFolderIdToBeMoved(Long folderIdToBeMoved) {
        this.folderIdToBeMoved = folderIdToBeMoved;
        return this;
    }

    public Long getDestinationFolderId() {
        return destinationFolderId;
    }

    public FolderRequest setDestinationFolderId(Long destinationFolderId) {
        this.destinationFolderId = destinationFolderId;
        return this;
    }

    public Long getId() {
        return id;
    }

    public FolderRequest setId(Long id) {
        this.id = id;
        return this;
    }
}
