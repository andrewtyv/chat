package com.lollychat.model;

import com.lollychat.model.ChatUser;
import com.lollychat.model.Friendship;
import com.lollychat.model.FriendshipStatus;
import com.lollychat.model.Room;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_request")
public class RoomRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ChatUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public RoomRequest() {
        this.createdAt = LocalDateTime.now();
    }

    public RoomRequest(Room room, ChatUser user, FriendshipStatus status) {
        this.room = room;
        this.user = user;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public ChatUser getUser() {
        return user;
    }

    public void setUser(ChatUser user) {
        this.user = user;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
