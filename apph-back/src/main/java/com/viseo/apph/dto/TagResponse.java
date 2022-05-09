package com.viseo.apph.dto;

import com.viseo.apph.domain.Tag;

import java.util.List;

public class TagResponse implements IResponseDTO {
    List<Tag> tags;

    public TagResponse(List<Tag> tags) {
        this.tags = tags;
    }
}
