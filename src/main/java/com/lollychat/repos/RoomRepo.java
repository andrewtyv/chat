package com.lollychat.repos;


import com.lollychat.model.ChatUser;
import com.lollychat.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepo extends JpaRepository<Room, Long> {
    List<Room> findByUsers(ChatUser user);
}
