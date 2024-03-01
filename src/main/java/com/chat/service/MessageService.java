package com.chat.service;

import com.chat.model.Chat;
import com.chat.model.Message;
import com.chat.model.MessageToUserRequest;
import com.chat.model.User;
import com.chat.model.request.MessageToChatRequest;
import com.chat.model.response.MessageResponse;
import com.chat.repository.ChatRepository;
import com.chat.repository.MessageRepository;
import com.chat.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class MessageService {

    private UserRepository userRepository;
    private MessageRepository messageRepository;
    private ChatRepository chatRepository;
    private JwtService jwtService;
    private ChatService chatService;

    public void sendMessageToUser(MessageToUserRequest messageRequest, HttpServletRequest servletRequest, HttpServletResponse response) {
        User sender = userRepository.findByEmail(jwtService.extractSubject(servletRequest)).orElseThrow();
        User receiver = userRepository.findById(messageRequest.getIdReceiver()).orElse(null);
        if(receiver == null) {
            response.setStatus(404);
        } else if(!Objects.equals(sender.getId(), receiver.getId())) {
            Long id = messageRepository.maxId();
            if(id == null) {
                id = 0L;
            }
            Message message = Message.builder()
                    .text(messageRequest.getContent())
                    .sentAt(new Date(System.currentTimeMillis()))
                    .sender(sender)
                    .id(id + 1)
                    .build();

            Chat chat = chatService.chatWithUser(sender, receiver);
            chat.getMessages().add(message);
            messageRepository.save(message);
            chatRepository.save(chat);
        }
    }

    public List<MessageResponse> messageFromChat(HttpServletRequest request, HttpServletResponse response, Long idChat) {
        User user = userRepository.findByEmail(jwtService.extractSubject(request)).orElseThrow();
        Chat chat = chatRepository.findById(idChat).orElse(null);

        if(chat == null) {
            response.setStatus(404);
            return null;
        } else {
            List<MessageResponse> messageResponses = new ArrayList<>();
            for(Message m: chat.getMessages()) {
                messageResponses.add(
                        MessageResponse.builder()
                                .idMsg(m.getId())
                                .content(m.getText())
                                .idSender(m.getSender().getId())
                                .sentAt(m.getSentAt())
                                .build()
                );
            }
            messageResponses.sort(MessageResponse::compareTo);
            return messageResponses;
        }
    }

    public void deleteMsg(HttpServletRequest request, HttpServletResponse response, Long idMsg) {
        User sender = userRepository.findByEmail(jwtService.extractSubject(request)).orElseThrow();
        Message msg = messageRepository.findById(idMsg).orElse(null);
        if(msg == null) {
            response.setStatus(404);
        } else if(msg.getSender().equals(sender)) {
            Chat chat = chatRepository.findByMessagesIn(Set.of(msg)).orElseThrow();
            chat.getMessages().remove(msg);
            chatRepository.save(chat);
            messageRepository.delete(msg);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }

    public void sendMessageToChat(
            HttpServletRequest servletRequest, MessageToChatRequest messageRequest,
            HttpServletResponse response
    ) {
        User sender = userRepository.findByEmail(jwtService.extractSubject(servletRequest)).orElseThrow();
        if(chatRepository.existsByMembersInAndId(Set.of(sender), messageRequest.getIdChat())) {
            Chat chat = chatRepository.findById(messageRequest.getIdChat()).orElseThrow();
            Long id = messageRepository.maxId();
            if(id == null) {
                id = 0L;
            }

            Message msg = Message.builder()
                    .sender(sender)
                    .text(messageRequest.getText())
                    .sentAt(new Date(System.currentTimeMillis()))
                    .id(id + 1)
                    .build();

            chat.getMessages().add(msg);

            messageRepository.save(msg);
            chatRepository.save(chat);
        } else {
            response.setStatus(404);
        }
    }

}