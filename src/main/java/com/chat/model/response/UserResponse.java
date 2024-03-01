package com.chat.model.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserResponse {

    private Long id;
    private String email;
    private String name;

}
