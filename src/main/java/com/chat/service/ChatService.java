package com.chat.service;

import com.chat.model.Chat;
import com.chat.model.User;
import com.chat.model.request.ChatRequest;
import com.chat.model.response.ChatResponse;
import com.chat.model.response.UserResponse;
import com.chat.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class ChatService {

    private UserRepository userRepository;
    private ChatRepository chatRepository;
    private MessageRepository messageRepository;

    private JwtService jwtService;

    public Chat chatWithUser(User first, User second) {
        Set<Chat> chats = first.equals(second) ? chatRepository.findAllByMembersIn(Set.of(first)) : chatRepository.findAllByMembersIn(Set.of(first, second));
        for (Chat chat : chats) {
            if (chat.getAdmin() == null && new HashSet<>(chat.getMembers()).containsAll(List.of(first, second))) {
                return chat;
            }
        }
        return Chat.builder()
                .messages(new ArrayList<>())
                .members(List.of(second, first))
                .build();
    }

    public void createChat(HttpServletRequest servletRequest, HttpServletResponse response, ChatRequest chatRequest) {
        User admin = userRepository.findByEmail(jwtService.extractSubject(servletRequest)).orElseThrow();
        Set<User> users = findAllUserById(chatRequest.getIdUsers());
        if(users == null) {
            response.setStatus(404);
        } else {
            users.add(admin);
            chatRepository.save(
                    Chat.builder()
                            .members(users.stream().toList())
                            .name(chatRequest.getName())
                            .admin(admin)
                            .build()
            );
        }
    }

    public void updateChat(HttpServletRequest servletRequest, HttpServletResponse response, ChatRequest chatRequest, Long idChat) {
        User admin = userRepository.findByEmail(jwtService.extractSubject(servletRequest)).orElseThrow();
        Chat chat  = chatRepository.findById(idChat).orElse(null);
        if(chat == null) {
            response.setStatus(404);
        } else if(!chat.getAdmin().getEmail().equals(admin.getEmail())) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        } else {
            Set<User> users = findAllUserById(chatRequest.getIdUsers());
            if(users == null) {
                response.setStatus(404);
                return;
            }
            users.add(admin);
            chat.getMembers().clear();
            for(User u: users) {
                chat.getMembers().add(u);
            }
            chat.setName(chatRequest.getName());
            chatRepository.save(chat);
        }
    }

    public Set<ChatResponse> findAllChatOfUser(HttpServletRequest request) {
        User user = userRepository.findByEmail(jwtService.extractSubject(request)).orElseThrow();
        Set<ChatResponse> chats = new HashSet<>();
        for(Chat chat: chatRepository.findAllByMembersIn(Set.of(user))) {
            chats.add(extractChatResponse(chat, user));
        }
        return chats;
    }

    //If user will be not found, this method return null
    private Set<User> findAllUserById(Long[] ids) {
        Set<User> users = new HashSet<>();
        for(Long id: ids) {
            User user = userRepository.findById(id).orElse(null);
            if(user == null) {
                return null;
            } else {
                users.add(user);
            }
        }
        return users;
    }

    private ChatResponse extractChatResponse(Chat chat, User user) {
        ChatResponse chatResponse = new ChatResponse();

        chatResponse.setId(chat.getId());

        for(User u: chat.getMembers()) {
            chatResponse.getMembers().add(UserResponse.builder()
                    .id(u.getId())
                    .name(u.getEmail())
                    .email(u.getName())
                    .build()
            );
        }

        if(chat.getAdmin() == null) {
            for(User u: chat.getMembers()) {
                if(!u.equals(user)) {
                    chatResponse.setName(u.getName());
                    break;
                }
            }
        } else {
            chatResponse.setAdmin(UserResponse.builder()
                    .name(chat.getAdmin().getName())
                    .email(chat.getAdmin().getEmail())
                    .id(chat.getAdmin().getId())
                    .build());
            chatResponse.setName(chat.getName());
        }
        return chatResponse;
    }

    public void leaveChat(HttpServletRequest request, HttpServletResponse response, Long chatId) {
        Chat chat = chatRepository.findById(chatId).orElse(null);
        if(chat == null) {
            response.setStatus(404);
        } else if(chat.getAdmin() != null) {
            User user = userRepository.findByEmail(jwtService.extractSubject(request)).orElseThrow();
            if(user.equals(chat.getAdmin())) {
                chatRepository.delete(chat);
            } else {
                chat.getMembers().remove(user);
                chatRepository.save(chat);
            }
        } else {
            chatRepository.delete(chat);
        }
    }

    public void deleteChat(HttpServletRequest request, HttpServletResponse response, Long chatId) {
        Chat chat = chatRepository.findById(chatId).orElse(null);
        User user = userRepository.findByEmail(jwtService.extractSubject(request)).orElseThrow();
        if(chat == null) {
            response.setStatus(404);
        } else if(chat.getAdmin() == null || chat.getAdmin().equals(user)) {
            chatRepository.delete(chat);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }

}

