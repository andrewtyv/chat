package com.lollychat.dto;

public class FriendshipDTO {
    private Long id;
    private String senderUsername;
    private String receiverUsername;
    private String status;

    public FriendshipDTO(Long id, String senderUsername, String receiverUsername, String status) {
        this.id = id;
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
