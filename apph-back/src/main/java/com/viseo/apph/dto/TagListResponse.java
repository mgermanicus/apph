package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TagListResponse implements IResponseDto {
    @JsonProperty("taglist")
    List<TagResponse> tagResponses;

    public TagListResponse(List<TagResponse> tagResponses) {
        this.tagResponses = tagResponses;
    }

    public List<TagResponse> getTagResponses() {
        return tagResponses;
    }
}
