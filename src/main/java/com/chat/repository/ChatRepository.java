package com.chat.repository;

import com.chat.model.Chat;
import com.chat.model.Message;
import com.chat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    Set<Chat> findAllByMembersIn(Set<User> members);
    Optional<Chat> findByMessagesIn(Set<Message> msg);
    boolean existsByMembersInAndId(Set<User> members, Long id);
    @Query(value = "select max(id) from chat", nativeQuery = true)
    Long maxId();

}
