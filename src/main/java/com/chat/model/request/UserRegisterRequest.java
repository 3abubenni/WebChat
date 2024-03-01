package com.chat.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Valid
public class UserRegisterRequest {

    @Size(min = 3)
    private String name;
    @Email
    private String email;
    @Size(min = 8)
    private String password;

}
