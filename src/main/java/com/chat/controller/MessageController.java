package com.chat.controller;

import com.chat.model.MessageToUserRequest;
import com.chat.model.request.MessageToChatRequest;
import com.chat.model.response.MessageResponse;
import com.chat.service.MessageService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Message controller")
@RequestMapping("/api/message/")
@AllArgsConstructor
public class MessageController {

    private MessageService messageService;

    @PostMapping("/user")
    @Operation(description = "Send message to user")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Message was sent successful"),
                    @ApiResponse(code = 404, message = "User is not found"),
                    @ApiResponse(code = 403, message = "User is unauthorized")
            }
    )
    public ResponseEntity sendMessageToUser(
            @RequestBody MessageToUserRequest messageRequest,
            HttpServletRequest servletRequest,
            HttpServletResponse response
    ) {
        messageService.sendMessageToUser(messageRequest, servletRequest, response);
        return ResponseEntity.status(response.getStatus()).build();
    }

    @GetMapping("/{idChat}")
    @Operation(description = "Get messages from chat with user")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Message was got successful"),
                    @ApiResponse(code = 404, message = "User is not found"),
                    @ApiResponse(code = 403, message = "User is unauthorized")
            }
    )
    public ResponseEntity<List<MessageResponse>> getMessagesFromChat(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable Long idChat
    ) {
        List<MessageResponse> messageResponses = messageService.messageFromChat(request, response, idChat);
        return ResponseEntity.status(response.getStatus()).body(messageResponses);
    }

    @DeleteMapping("/{idMsg}")
    @Operation(description = "Delete message which user sent")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Message was deleted successful"),
                    @ApiResponse(code = 406, message = "Message wasn't sent by user"),
                    @ApiResponse(code = 404, message = "Message isn't found"),
                    @ApiResponse(code = 403, message = "User is unauthorized")
            }
    )
    public ResponseEntity deleteMsg(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable Long idMsg
    ) {
        messageService.deleteMsg(request, response, idMsg);
        return ResponseEntity.status(response.getStatus()).build();
    }

    @PostMapping("/chat")
    @Operation(description = "Send message to user")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Message was sent successful"),
                    @ApiResponse(code = 404, message = "User is not found"),
                    @ApiResponse(code = 403, message = "User is unauthorized")
            }
    )
    public ResponseEntity sendMessageToChat(
            HttpServletRequest servletRequest,
            @RequestBody MessageToChatRequest msg,
            HttpServletResponse response
    ) {
       messageService.sendMessageToChat(servletRequest, msg, response);
       return ResponseEntity.status(response.getStatus()).build();
    }

}
