package com.viseo.apph.domain;

public class Location {
    String address;
    Position position;

    public String getAddress() {
        return address;
    }

    public Location setAddress(String address) {
        this.address = address;
        return this;
    }

    public Position getPosition() {
        return position;
    }

    public Location setPosition(Position position) {
        this.position = position;
        return this;
    }
}
