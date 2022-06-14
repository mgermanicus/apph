package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FolderRequest {
    @JsonProperty("name")
    String name;
    @JsonProperty("parentFolderId")
    Long parentFolderId;
    @JsonProperty("folderToBeMoved")
    Long folderToBeMoved;
    @JsonProperty("moveTo")
    Long moveTo;
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

    public Long getFolderToBeMoved() {
        return folderToBeMoved;
    }

    public FolderRequest setFolderToBeMoved(Long folderToBeMoved) {
        this.folderToBeMoved = folderToBeMoved;
        return this;
    }

    public Long getMoveTo() {
        return moveTo;
    }

    public FolderRequest setMoveTo(Long moveTo) {
        this.moveTo = moveTo;
        return this;
    }
}
