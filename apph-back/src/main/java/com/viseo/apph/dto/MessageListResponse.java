package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class MessageListResponse implements IResponseDto {
    @JsonProperty("messageList")
    List<String> messageList;

    public MessageListResponse() {
        this.messageList = new ArrayList<>();
    }

    public MessageListResponse addMessage(String message) {
        this.messageList.add(message);
        return this;
    }

    public List<String> getMessageList() {
        return this.messageList;
    }
}
