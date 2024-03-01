package com.chat.model.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class MsgToUserRequest {

    private Long idReceiver;
    private String text;

}
