package com.lollychat.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FriendListhandler {
    private String friendName;
    private String createdAt;
    private String status;
    //казав що потом буду май много вертати ізза того тут білдер
    private FriendListhandler(Builder builder){
        this.friendName = builder.friendName;
        this.createdAt = builder.createdAt;
        this.status= builder.status;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Builder{
        private String friendName;
        private String createdAt;
        private String status;

        public Builder friendName(String friendName){
            this.friendName = friendName;
            return this;
        }
        public Builder createdAt(LocalDateTime Time){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            this.createdAt = Time.format(formatter);
            return this;
        }
        public Builder status(String status){
            this.status = status;
            return this;
        }
        public FriendListhandler build (){
            return new FriendListhandler(this);
        }
    }

}
