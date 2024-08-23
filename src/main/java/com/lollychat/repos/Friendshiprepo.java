package com.lollychat.repos;

import com.lollychat.model.ChatUser;
import com.lollychat.model.Friendship;
import com.lollychat.model.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Friendshiprepo extends JpaRepository<Friendship, Long> {

    // Знаходження дружби за отримувачем та статусом
    List<Friendship> findByReceiverAndStatus(ChatUser receiver, FriendshipStatus status);

    // Знаходження дружби за відправником та статусом
    List<Friendship> findBySenderAndStatus(ChatUser sender, FriendshipStatus status);

    // Знаходження дружби за відправником та отримувачем
    List<Friendship> findBySenderAndReceiver(ChatUser sender, ChatUser receiver);
}
