package com.example.chatservice.core.WebSockets;


import com.example.chatservice.core.exceptions.GeneralException;
import com.example.chatservice.core.helpers.JwtService;
import com.example.chatservice.rest.feign.UserService;
import com.example.chatservice.rest.models.UserModel;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class MainSocketHandler implements WebSocketHandler {
  private final JwtService jwtService;
  private final UserService userService;
  public Map<String, WebSocketSession> sessions = new HashMap<>();

  public MainSocketHandler(JwtService jwtService, UserService userService) {
    this.jwtService = jwtService;
    this.userService = userService;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    UserModel user = getUser(session, session.getUri().getQuery().split("=")[1]);
    if (user == null) {
      return;
    }

    sessions.put(user.getId(), session);
    System.out.println("Session created for the user " + user.getId() + " where the session id is " + session.getId());
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    System.out.println("Error happened " + session.getId() + " with reason ### " + exception.getMessage());
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
    System.out.println("Connection closed for session " + session.getId() + " with status ### " + closeStatus.getReason());
  }

  @Override
  public boolean supportsPartialMessages() {
    return false;
  }

  @Override
  public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    String messageReceived = (String) message.getPayload();
    System.out.println("Message received: " + messageReceived);
  }

  public void broadcastMessage(String message) throws IOException {
    sessions.forEach((key, session) -> {
      try {
        if (session.isOpen()) {
          session.sendMessage(new TextMessage(message));
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public void sendMessage(String userId, String message) {
    WebSocketSession session = sessions.get(userId);
    if (session == null) {
      return;
    }

    try {
      session.sendMessage(new TextMessage(message));
    } catch (IOException e) {
      throw new GeneralException(e);
    }
  }

  private UserModel getUser(WebSocketSession session, String jwt) throws IOException {
    if (jwt.isEmpty()) {
      session.close();
      return null;
    }

    String userEmail = (String) jwtService.decode(jwt).get("email");

    return userService.getByEmail(userEmail).orElse(null);
  }
}