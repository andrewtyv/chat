package com.lollychat.repos;

import com.lollychat.model.ChatUser;
import com.lollychat.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo  extends JpaRepository<Message,Long> {

}
