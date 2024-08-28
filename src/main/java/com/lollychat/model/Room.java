package com.lollychat.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "room_users",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<ChatUser> users = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomRequest> roomRequests = new ArrayList<>();

    public void addUserRequest(ChatUser user, FriendshipStatus status) {
        RoomRequest request = new RoomRequest(this, user, status);
        roomRequests.add(request);
    }

    public void processUserRequest(ChatUser user, boolean accept) {
        RoomRequest request = roomRequests.stream()
                .filter(r -> r.getUser().equals(user))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (accept) {
            roomRequests.remove(request);
            users.add(user);
            request.setStatus(FriendshipStatus.ACCEPTED);
        } else {
            roomRequests.remove(request);
            request.setStatus(FriendshipStatus.REJECTED);
        }
    }
    public Room(String name) {
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    protected Room() {
        this.createdAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<RoomRequest> getRoomRequests() {
        return roomRequests;
    }

    public void setRoomRequests(List<RoomRequest> roomRequests) {
        this.roomRequests = roomRequests;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<ChatUser> getUsers() {
        return users;
    }

    public void setUsers(List<ChatUser> users) {
        this.users = users;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
        message.setRoom(this);
    }

    public void removeMessage(Message message) {
        messages.remove(message);
        message.setRoom(null);
    }
}
