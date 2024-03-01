package com.chat.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
public class MessageToChatRequest {

    private Long idChat;
    private String text;

}
