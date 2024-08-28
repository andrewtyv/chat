package com.lollychat.repos;

import com.lollychat.model.StringMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StringMessageRepo extends JpaRepository<StringMessage, Long> {

}
