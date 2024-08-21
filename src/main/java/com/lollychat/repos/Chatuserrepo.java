package com.lollychat.repos;

import com.lollychat.model.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Chatuserrepo extends JpaRepository<ChatUser,Long> {
    boolean existsByUsername(String username);
}
