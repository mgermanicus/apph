package com.viseo.apph.dto;

public class PhotoResponse implements IResponseDTO {
    byte[] data;

    String name;

    String extension;

    public byte[] getData() {
        return data;
    }

    public PhotoResponse setData(byte[] data) {
        this.data = data;
        return this;
    }

    public String getName() {
        return name;
    }

    public PhotoResponse setName(String name) {
        this.name = name;
        return this;
    }

    public String getExtension() {
        return extension;
    }

    public PhotoResponse setExtension(String extension) {
        this.extension = extension;
        return this;
    }
}
