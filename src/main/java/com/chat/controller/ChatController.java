package com.chat.controller;

import com.chat.model.request.ChatRequest;
import com.chat.model.response.ChatResponse;
import com.chat.service.ChatService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/chat")
@Tag(
        name = "ChatController",
        description = "It controls the logic of chats"
)
@AllArgsConstructor
public class ChatController {

    private ChatService chatService;

    @PostMapping
    @Operation(description = "Create Chat")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Chat was succsesful created"),
                    @ApiResponse(code = 404, message = "One or more users were not found"),
                    @ApiResponse(code = 400, message = "Name chat is null")
            }
    )
    public ResponseEntity createChat(
            @RequestBody @Valid ChatRequest chatRequest,
            BindingResult bindingResult,
            HttpServletRequest servletRequest,
            HttpServletResponse response
    ) {
        if(bindingResult.hasErrors()) {
            return ResponseEntity.status(400).build();
        }
        chatService.createChat(servletRequest, response, chatRequest);
        return ResponseEntity.status(response.getStatus()).build();
    }

    @PostMapping("/{id}")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Chat was succsesful created"),
                    @ApiResponse(code = 404, message = "Chat or one or more users were not found"),
                    @ApiResponse(code = 400, message = "Name chat is null"),
                    @ApiResponse(code = 401, message = "User isn't admin")
            }
    )
    @Operation(description = "Update Chat")
    public ResponseEntity updateChat(
            @RequestBody @Valid ChatRequest chatRequest,
            BindingResult bindingResult,
            HttpServletRequest servletRequest,
            HttpServletResponse response,
            @PathVariable Long id
    ) {
        if(bindingResult.hasErrors()) {
            return ResponseEntity.status(400).build();
        }
        chatService.updateChat(servletRequest, response, chatRequest, id);
        return ResponseEntity.status(response.getStatus()).build();
    }

    @GetMapping
    @ApiResponse(
            code = 200, message = "Successful operation"
    )
    @Operation
    public ResponseEntity<Set<ChatResponse>> chats(
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(chatService.findAllChatOfUser(request));
    }

    @DeleteMapping("/{idChat}")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Chat is deleted"),
                    @ApiResponse(code = 404, message = "Chat is not found"),
                    @ApiResponse(code = 406, message = "User is not admin")
            }
    )
    @Operation(description = "Delete Chat. If chat is private \"User to User\", then chat will be deleted too")
    public ResponseEntity deleteChat(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable Long idChat
    ) {
        chatService.deleteChat(request, response, idChat);
        return ResponseEntity.status(response.getStatus()).build();
    }

    @PatchMapping("/{idChat}")
    @Operation(description = "Leave Chat. If admin leaves chat or chat is private, then chat will be deleted")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Chat is deleted"),
                    @ApiResponse(code = 404, message = "Chat is not found")
            }
    )
    public ResponseEntity leaveChat(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable Long idChat
    ) {
        chatService.leaveChat(request, response, idChat);
        return ResponseEntity.status(response.getStatus()).build();
    }

}
