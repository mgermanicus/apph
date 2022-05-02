package com.viseo.apph.controller;

import com.viseo.apph.domain.Tag;
import com.viseo.apph.dto.MessageResponse;
import com.viseo.apph.dto.TagRequest;
import com.viseo.apph.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class TagController {
    @Autowired
    TagService tagService;

    @PostMapping("/")
    public ResponseEntity createTag(@RequestBody TagRequest tagRequest) {
        try {
            Tag tag = new Tag().setUser(tagRequest.getUser()).setName(tagRequest.getName());
            return ResponseEntity.ok(new MessageResponse(tagService.createTag(tag)));
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return null;
    }
}
