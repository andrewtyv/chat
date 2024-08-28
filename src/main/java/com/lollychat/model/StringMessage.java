package com.lollychat.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("string")
public class StringMessage extends Message {
    @Column(nullable = false)
    private String content;

    public StringMessage(Room room, ChatUser author, String content) {
        super(room, author);
        this.content = content;
    }

    protected StringMessage() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
