package com.lollychat.dto;

import java.time.LocalDateTime;

public class FriendshipDTO {
    private Long id;
    private String senderUsername;
    private String receiverUsername;
    private String status;
    private LocalDateTime createdAt;

    private FriendshipDTO(Builder builder) {
        this.id = builder.id;
        this.senderUsername = builder.senderUsername;
        this.receiverUsername = builder.receiverUsername;
        this.status = builder.status;
        this.createdAt=builder.createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Builder{
        private Long id ;
        private String senderUsername;
        private String receiverUsername;
        private String status;
        private LocalDateTime createdAt;
        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder sender(String sender) {
            this.senderUsername = sender;
            return this;
        }

        public Builder receiver(String receiver) {
            this.receiverUsername = receiver;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }
        public Builder createdAt(LocalDateTime time){
            this.createdAt = time;
            return this;
        }
        public FriendshipDTO build() {
            return new FriendshipDTO(this);
        }





    }
}
