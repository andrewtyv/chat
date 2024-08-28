package com.lollychat.dto;

public class RoomDTO {
    private String message;
    private String name;
    private Long id;
    private String createdAt;

    public RoomDTO(String message, String name, Long id, String createdAt) {
        this.message = message;
        this.name = name;
        this.id = id;
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
