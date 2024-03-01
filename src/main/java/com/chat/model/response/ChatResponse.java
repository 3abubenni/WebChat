package com.chat.model.response;

import com.chat.model.User;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ChatResponse {
    private Long id;
    private String name;
    private final List<UserResponse> members = new ArrayList<>();
    private UserResponse admin;

}
