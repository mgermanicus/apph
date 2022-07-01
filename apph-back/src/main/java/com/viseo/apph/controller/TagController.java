package com.viseo.apph.controller;

import com.viseo.apph.domain.Tag;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.IResponseDto;
import com.viseo.apph.dto.MessageResponse;
import com.viseo.apph.dto.TagListResponse;
import com.viseo.apph.security.Utils;
import com.viseo.apph.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/tag")
public class TagController {
    @Autowired
    Utils utils;
    @Autowired
    private TagService tagService;

    @GetMapping("/")
    public ResponseEntity getTags() {
        User user = utils.getUser();
        List<Tag> tags = tagService.getTags(user);
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/count")
    public ResponseEntity<IResponseDto> getTagsWithCount() {
        User user = utils.getUser();
        TagListResponse tags = tagService.getTagsWithCount(user);
        return ResponseEntity.ok(tags);
    }
}
