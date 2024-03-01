package com.chat.model.response;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MessageResponse implements Comparable<MessageResponse> {

    private Long idSender;
    private Long idMsg;
    private String content;
    private Date sentAt;

    @Override
    public int compareTo(MessageResponse o) {
        return sentAt.compareTo(o.sentAt);
    }
}
