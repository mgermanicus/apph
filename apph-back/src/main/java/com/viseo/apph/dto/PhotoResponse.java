package com.viseo.apph.dto;

public class PhotoResponse implements IResponseDTO {
    byte[] data;

    public byte[] getData() {
        return data;
    }

    public PhotoResponse setData(byte[] data) {
        this.data = data;
        return this;
    }
}
