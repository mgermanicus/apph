package com.viseo.apph.domain;

public class Position {
    Float lat;
    Float lng;

    public Float getLat() {
        return lat;
    }

    public Position setLat(Float lat) {
        this.lat = lat;
        return this;
    }

    public Float getLng() {
        return lng;
    }

    public Position setLng(Float lng) {
        this.lng = lng;
        return this;
    }
}
