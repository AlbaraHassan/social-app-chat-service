package com.example.chatservice.rest.controllers;


import com.example.chatservice.core.auth.Auth;
import com.example.chatservice.rest.dtos.MessageDTO;
import com.example.chatservice.rest.dtos.UserDTO;
import com.example.chatservice.rest.models.ChatModel;
import com.example.chatservice.rest.services.ChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/chat")
@Tag(name = "Chat")
public class ChatController {
  private final ChatService chatService;

  public ChatController(ChatService chatService) {
    this.chatService = chatService;
  }

  @PostMapping("/send")
  @Auth
  public ResponseEntity<Void> sendChatMessage(@RequestParam String userId, @RequestBody MessageDTO message) throws IOException {
    this.chatService.send(userId, message.getMessage());
    return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
  }

  @GetMapping
  @Auth
  public List<ChatModel> getAll() {
    return this.chatService.getAll();
  }

  @Auth
  @GetMapping("search")
  public List<UserDTO> search(@RequestParam String userName) {
    return this.chatService.search(userName);
  }

  @Auth
  @PatchMapping
  public ResponseEntity<Boolean> update(@RequestParam String id) {
    return ResponseEntity.ok(this.chatService.update(id));
  }
}
