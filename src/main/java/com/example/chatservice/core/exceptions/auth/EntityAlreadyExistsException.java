package com.example.chatservice.core.exceptions.auth;

import com.example.chatservice.core.exceptions.GeneralException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class EntityAlreadyExistsException extends GeneralException {
  public EntityAlreadyExistsException() {
    super(HttpStatus.CONFLICT.value());
  }

  public EntityAlreadyExistsException(String message) {
    super(HttpStatus.CONFLICT.value(), message);
  }
}
