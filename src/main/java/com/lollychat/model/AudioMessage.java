package com.lollychat.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("audio")
public class AudioMessage extends Message {
    @Column(nullable = false)
    private byte[] audioData;

    public AudioMessage(Room room, ChatUser author, byte[] audioData) {
        super(room, author);
        this.audioData = audioData;
    }

    protected AudioMessage() {
    }

    public byte[] getAudioData() {
        return audioData;
    }

    public void setAudioData(byte[] audioData) {
        this.audioData = audioData;
    }
}
