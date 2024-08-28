package com.lollychat.repos;

import com.lollychat.model.ChatUser;
import com.lollychat.model.FriendshipStatus;
import com.lollychat.model.Room;
import com.lollychat.model.RoomRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRequestRepo extends JpaRepository<RoomRequest, Long> {
    RoomRequest findByRoomAndUser(Room room, ChatUser user);

    List<RoomRequest> findByUserAndStatus(ChatUser user, FriendshipStatus status);
}
