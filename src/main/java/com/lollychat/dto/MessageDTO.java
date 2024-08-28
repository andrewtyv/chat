package com.lollychat.dto;

public class MessageDTO {
    private Long id;
    private Long roomId;
    private String authorUsername;
    private boolean read;
    private String content;


    public MessageDTO(Long id, Long roomId, String authorUsername, boolean read, String content) {
        this.id = id;
        this.roomId = roomId;
        this.authorUsername = authorUsername;
        this.read = read;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
