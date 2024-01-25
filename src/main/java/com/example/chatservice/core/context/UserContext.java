package com.example.chatservice.core.context;


import com.example.chatservice.rest.dtos.UserDTO;
import com.example.chatservice.rest.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserContext {
  private final ThreadLocal<UserDTO> userContextThreadLocal = new ThreadLocal<>();

  public String getId() {
    return this.userContextThreadLocal.get().getId();
  }
  public Role getRole() {
    return this.userContextThreadLocal.get().getRole();
  }

  public String getEmail() {
    return this.userContextThreadLocal.get().getEmail();
  }

  public String getUsername() {
    return this.userContextThreadLocal.get().getUserName();
  }

  public void setUser(UserDTO user) {
    this.userContextThreadLocal.set(user);
  }

  public void clear() {
    this.userContextThreadLocal.remove();
  }
}
