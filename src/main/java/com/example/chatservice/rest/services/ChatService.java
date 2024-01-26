package com.example.chatservice.rest.services;


import com.example.chatservice.core.WebSockets.MainSocketHandler;
import com.example.chatservice.core.context.UserContext;
import com.example.chatservice.core.exceptions.general.NotFoundException;
import com.example.chatservice.core.repositories.ChatRepository;
import com.example.chatservice.rest.dtos.MessageDTO;
import com.example.chatservice.rest.dtos.UserDTO;
import com.example.chatservice.rest.feign.UserService;
import com.example.chatservice.rest.models.ChatModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ChatService {

  private final ChatRepository chatRepository;
  private final MainSocketHandler mainSocketHandler;
  private final UserContext userContext;
  private final UserService userService;

  public ChatService(ChatRepository chatRepository, MainSocketHandler mainSocketHandler, UserContext userContext, UserService userService) {
    this.chatRepository = chatRepository;
    this.mainSocketHandler = mainSocketHandler;
    this.userContext = userContext;
    this.userService = userService;
  }

  private void create(MessageDTO message, List<UserDTO> members) {
    List<MessageDTO> messages = List.of(message);
    ChatModel chatModel = new ChatModel(members, messages);
    this.chatRepository.save(chatModel);
  }

  public void send(String userId, String message) {
    UserDTO sender = this.userService.get(this.userContext.getId()).orElseThrow(NotFoundException::new);
    UserDTO receiver = this.userService.get(userId).orElseThrow(NotFoundException::new);
    List<UserDTO> members = new ArrayList<>(List.of(sender, receiver));
    members.sort(Comparator.comparing(UserDTO::getId)); //needed to not look at the order of members
    MessageDTO messageDTO = new MessageDTO(message, sender);
    if (this.chatRepository.existsChatModelByMembers(members)) {
      ChatModel chatModel = this.chatRepository.findChatModelByMembers(members);
      chatModel.addMessage(messageDTO);
      this.chatRepository.save(chatModel);
    } else {
      this.create(messageDTO, members);
    }

    mainSocketHandler.sendMessage(userId, messageDTO.toString());
  }

  public boolean update(String id) {
    ChatModel chatModel = this.chatRepository.findById(id).orElseThrow(NotFoundException::new);
    chatModel.getMessages().forEach(messageDTO -> {
      if (!messageDTO.getSender().getId().equals(this.userContext.getId())) {
        if(!messageDTO.isSeen()) {
          messageDTO.setSeen(true);
          chatModel.setNewMessages();
        }
      }
    });
    this.chatRepository.save(chatModel);

    return true;
  }

  public List<UserDTO> search(String userName) {
    return this.userService.search(userName).stream().map(UserDTO::new).filter(userDTO -> !userDTO.getUserName().equals(this.userContext.getUsername()) && !this.exists(userDTO.getUserName())).toList();
  }

  public boolean exists(String userName) {
    UserDTO sender = this.userService.get(this.userContext.getId()).orElseThrow(NotFoundException::new);
    UserDTO receiver = this.userService.getByUsername(userName).orElseThrow(NotFoundException::new);
    List<UserDTO> members = new ArrayList<>(List.of(sender, receiver));
    members.sort(Comparator.comparing(UserDTO::getId));
    System.out.println(this.chatRepository.existsChatModelByMembers(members));
    return this.chatRepository.existsChatModelByMembers(members);
  }


  public List<ChatModel> getAll() {
    UserDTO user = this.userService.get(this.userContext.getId()).orElseThrow(NotFoundException::new);
    return this.chatRepository.findAllByMembersContaining(user);
  }
}
