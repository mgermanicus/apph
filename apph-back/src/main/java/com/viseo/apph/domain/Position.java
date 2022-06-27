package com.viseo.apph.domain;

public class Position {
    float lat;
    float lng;

    public float getLat() {
        return lat;
    }

    public Position setLat(float lat) {
        this.lat = lat;
        return this;
    }

    public float getLng() {
        return lng;
    }

    public Position setLng(float lng) {
        this.lng = lng;
        return this;
    }
}
