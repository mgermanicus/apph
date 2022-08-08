package com.viseo.apph.dto;

public class MapMarker {
    long id;
    Float lat;
    Float lng;

    public long getId() { return id; }

    public MapMarker setId(long id) {
        this.id = id;
        return this;
    }
    public Float getLat() {
        return lat;
    }

    public MapMarker setLat(Float lat) {
        this.lat = lat;
        return this;
    }

    public Float getLng() {
        return lng;
    }

    public MapMarker setLng(Float lng) {
        this.lng = lng;
        return this;
    }
}
