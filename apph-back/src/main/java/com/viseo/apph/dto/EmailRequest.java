package com.viseo.apph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailRequest {
    @JsonProperty("recipient")
    String recipient;
    @JsonProperty("subject")
    String subject;
    @JsonProperty("content")
    String content;
    @JsonProperty("ids")
    long[] ids;

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public long[] getIds() {
        return ids;
    }
}
