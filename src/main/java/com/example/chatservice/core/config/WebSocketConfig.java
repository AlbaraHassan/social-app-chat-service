package com.example.chatservice.core.config;

import com.example.chatservice.core.WebSockets.MainSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
  private final MainSocketHandler mainSocketHandler;
  public WebSocketConfig(MainSocketHandler mainSocketHandler){
    this.mainSocketHandler = mainSocketHandler;
  }

  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(mainSocketHandler, "/ws").setAllowedOrigins("*");
  }
}