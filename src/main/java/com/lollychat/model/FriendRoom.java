package com.lollychat.model;

import jakarta.persistence.Entity;

@Entity
public class FriendRoom extends Room {

    public FriendRoom(String name) {
        super(name);
    }

    protected FriendRoom() {
        super();
    }

    @Override
    public void addUser(ChatUser user) {
        if (getUsers().size() >= 2) {
            throw new IllegalStateException("FriendRoom can only have two users.");
        }
        super.addUser(user);
    }
}
