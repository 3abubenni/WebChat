package com.chat.model.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Valid
@Getter
public class UserPatchRequest {

    @Size(min = 3)
    private String name;
    @Size(min = 8)
    private String password;

}
