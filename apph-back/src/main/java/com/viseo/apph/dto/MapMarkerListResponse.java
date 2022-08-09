package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class MapMarkerListResponse implements IResponseDto {
    @JsonProperty("markerList")
    List<MapMarker> markerList;


    public MapMarkerListResponse() {
        this.markerList = new ArrayList<>();
    }

    public MapMarkerListResponse addMarker(MapMarker marker) {
        this.markerList.add(marker);
        return this;
    }

    public List<MapMarker> getMarkerList() {
        return this.markerList;
    }
}
