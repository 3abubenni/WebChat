package com.chat.controller;

import com.chat.model.request.UserLoginRequest;
import com.chat.model.request.UserPatchRequest;
import com.chat.model.request.UserRegisterRequest;
import com.chat.model.response.TokenResponse;
import com.chat.model.response.UserResponse;
import com.chat.service.UserService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@Tag(
        name = "Account Controller",
        description = "Controller for register, login and getting information about users"
)
@AllArgsConstructor
public class AccountController {

    private UserService userService;

    @PostMapping("/login")
    @Operation(description = "Login")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful login"),
                    @ApiResponse(code = 403, message = "Not Authorized"),
            }
    )
    public ResponseEntity<TokenResponse> login(
            @RequestBody UserLoginRequest loginRequest
    ) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @PostMapping("/register")
    @Operation(description = "Registration of new user")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful register"),
                    @ApiResponse(code = 400, message = "Not valid request"),
                    @ApiResponse(code = 406, message = "User with same email already exists")
            }
    )
    public ResponseEntity<TokenResponse> register(
            @RequestBody @Valid UserRegisterRequest registerRequest,
            BindingResult result
    ) {
        if(result.hasErrors()) {
            return ResponseEntity.status(400).build();
        }

        TokenResponse response = userService.register(registerRequest);
        return response == null ?
                ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
                : ResponseEntity.status(201).body(response);
    }

    @GetMapping
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful getting information"),
                    @ApiResponse(code = 403, message = "User is unauthorized")
            }
    )
    @Operation(description = "Get information about your authorized account")
    public ResponseEntity<UserResponse> me(
            HttpServletRequest request
    ) {
       return ResponseEntity.ok(userService.me(request));
    }

    @GetMapping("/{email}")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successful getting information about found accounts"),
                    @ApiResponse(code = 403, message = "User is unauthorized")
            }
    )
    @Operation(description = "Search users by email")
    public ResponseEntity<List<UserResponse>> searchUsersByEmail(
            @PathVariable String email
    ) {
        return ResponseEntity.ok(userService.searchUserByEmail(email));
    }

    @PatchMapping
    @ApiResponses(
            value = {
                    @ApiResponse(code = 400, message = "Not valid request"),
                    @ApiResponse(code = 200, message = "Successful patching"),
                    @ApiResponse(code = 403, message = "User is unauthorized")
            }
    )
    @Operation(description = "Patch information about user (password and name)")
    public ResponseEntity patchUser(
            HttpServletRequest request,
            @RequestBody @Valid UserPatchRequest patchRequest,
            BindingResult result
    ) {
        if(result.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            userService.patchUser(patchRequest, request);
            return ResponseEntity.ok().build();
        }
    }

    @PostMapping("/logout")
    @Operation(description = "Logout user")
    @ApiResponses(
                value = {
                    @ApiResponse(code = 200, message = "User is logout"),
                    @ApiResponse(code = 403, message = "User is unauthorized")
                }
    )
    public ResponseEntity logout(
            HttpServletRequest request
    ) {
        userService.logout(request);
        return ResponseEntity.ok().build();
    }

}
