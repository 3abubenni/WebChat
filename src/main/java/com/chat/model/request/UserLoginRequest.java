package com.chat.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class UserLoginRequest {

    private String email;
    private String password;

}
