package com.lollychat.repos;

import com.lollychat.model.ChatUser;
import com.lollychat.model.Friendship;
import com.lollychat.model.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface Friendshiprepo extends JpaRepository<Friendship, Long> {

    List<Friendship> findByReceiverAndStatus(ChatUser receiver, FriendshipStatus status);

    List<Friendship> findBySenderAndStatus(ChatUser sender, FriendshipStatus status);

    List<Friendship> findBySenderAndReceiver(ChatUser sender, ChatUser receiver);

    @Transactional
    void deleteBySenderOrReceiver(ChatUser user, ChatUser user1);
}
