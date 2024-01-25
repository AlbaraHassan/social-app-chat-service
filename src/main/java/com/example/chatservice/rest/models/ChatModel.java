package com.example.chatservice.rest.models;


import com.example.chatservice.rest.dtos.MessageDTO;
import com.example.chatservice.rest.dtos.UserDTO;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document("chat")
public class ChatModel {

  @Id
  @NotBlank
  private String id;

  @NotBlank
  private List<UserDTO> members;
  @NotBlank
  private List<MessageDTO> messages = new ArrayList<>();

  @NotBlank
  private int newMessages = 0;

  public int getNewMessages() {
    return newMessages;
  }

  public void setNewMessages() {
    this.newMessages--;
  }

  public ChatModel(List<UserDTO> members, List<MessageDTO> messages) {
    this.members = members;
    this.messages = messages;
    this.newMessages++;
  }
  public ChatModel() {}


  public ChatModel(List<UserDTO> members) {
    this.members = members;
  }



  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<UserDTO> getMembers() {
    return members;
  }

  public void setMembers(List<UserDTO> members) {
    this.members = members;
  }

  public List<MessageDTO> getMessages() {
    return messages;
  }

  public void setMessages(List<MessageDTO> messages) {
    this.messages = messages;
  }

  public void addMessage(MessageDTO message){
    this.messages.add(message);
    this.newMessages++;
  }

}
