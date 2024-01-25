package com.example.chatservice.rest.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;

public class MessageDTO {
  @NotBlank
  private String message;
  private UserDTO sender;

  private boolean isSeen = false;

  public boolean isSeen() {
    return isSeen;
  }

  public void setSeen(boolean seen) {
    isSeen = seen;
  }

  public UserDTO getSender() {
    return sender;
  }

  public void setSender(UserDTO sender) {
    this.sender = sender;
  }

  public MessageDTO() {}

  public MessageDTO(String message) {
    this.message = message;
  }

  public MessageDTO(String message, UserDTO sender){
    this.message = message;
    this.sender = sender;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(this);
    } catch (JsonProcessingException ignored) {
      return "Failed";
    }
  }
}