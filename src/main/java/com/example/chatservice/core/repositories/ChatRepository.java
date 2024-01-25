package com.example.chatservice.core.repositories;

import com.example.chatservice.rest.dtos.UserDTO;
import com.example.chatservice.rest.models.ChatModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChatRepository extends MongoRepository<ChatModel, String> {
  boolean existsChatModelByMembers(List<UserDTO> members);
  ChatModel findChatModelByMembers(List<UserDTO> members);
  List<ChatModel> findAllByMembersContaining(UserDTO member);

}
