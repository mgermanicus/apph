package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class FolderResponse implements IResponseDto {
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

    @JsonProperty("data")
    byte[] data;

    @JsonProperty("format")
    String format;

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

    public byte[] getData() {
        return data;
    }

    public FolderResponse setData(byte[] data) {
        this.data = data;
        return this;
    }

    public String getFormat() {
        return format;
    }

    public FolderResponse setFormat(String format) {
        this.format = format;
        return this;
    }
}
