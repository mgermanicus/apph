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
    @JsonProperty("type")
    String type;

    public String getRecipient() {
        return recipient;
    }

    public EmailRequest setRecipient(String recipient) {
        this.recipient = recipient;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public EmailRequest setSubject (String subject) {
        this.subject = subject;
        return this;
    }

    public String getContent() {
        return content;
    }

    public EmailRequest setContent(String content) {
        this.content = content;
        return this;
    }

    public long[] getIds() {
        return ids;
    }

    public EmailRequest setIds(long[] ids) {
        this.ids = ids;
        return this;
    }

    public String getType() {
        return type;
    }

    public EmailRequest setType(String type) {
        this.type = type;
        return this;
    }
}
