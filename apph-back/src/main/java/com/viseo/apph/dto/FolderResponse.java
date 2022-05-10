package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class FolderResponse implements IResponseDTO {
    @JsonProperty("id")
    long id;

    @JsonProperty("version")
    long version;

    @JsonProperty("name")
    String name;

    @JsonProperty("parentFolderId")
    Long parentFolderId;

    @JsonProperty("childrenFolders")
    List<FolderResponse> childrenFolders;

    public FolderResponse() {
        this.childrenFolders = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public FolderResponse setId(long id) {
        this.id = id;
        return this;
    }

    public long getVersion() {
        return version;
    }

    public FolderResponse setVersion(long version) {
        this.version = version;
        return this;
    }

    public String getName() {
        return name;
    }

    public FolderResponse setName(String name) {
        this.name = name;
        return this;
    }

    public Long getParentFolderId() {
        return parentFolderId;
    }

    public FolderResponse setParentFolderId(Long parentFolderId) {
        this.parentFolderId = parentFolderId;
        return this;
    }

    public FolderResponse addChildFolder(FolderResponse folder) {
        this.childrenFolders.add(folder);
        return this;
    }

    public List<FolderResponse> getChildrenFolders() {
        return childrenFolders;
    }
}
