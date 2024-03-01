package com.chat.repository;

import com.chat.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query(value = "select max(id) from message", nativeQuery = true)
    Long maxId();

}
