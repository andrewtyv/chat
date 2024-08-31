package com.lollychat.repos;

import com.lollychat.model.ChatUser;
import com.lollychat.model.GroopRoom;
import com.lollychat.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroopRoomRepo extends JpaRepository<GroopRoom , Long> {
    List<Room> findByUsers(ChatUser user);

}
