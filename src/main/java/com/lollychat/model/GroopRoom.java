package com.lollychat.model;

import jakarta.persistence.Entity;

@Entity
public class GroopRoom extends Room {

    public GroopRoom(String name) {
        super(name);
    }

    protected GroopRoom() {
        super();
    }
}
