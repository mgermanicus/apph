package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.viseo.apph.domain.Tag;

import java.util.List;

public class TagResponse implements IResponseDTO {
    @JsonProperty("tags")
    List<Tag> tags;
    @JsonProperty("tag")
    Tag tag;

    public TagResponse(List<Tag> tags) {
        this.tags = tags;
    }

    public TagResponse(Tag tag) {
        this.tag = tag;
    }
}
