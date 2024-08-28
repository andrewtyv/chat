package com.lollychat.repos;

import com.lollychat.model.AudioMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioMessageRepo extends JpaRepository<AudioMessage, Long> {
}
