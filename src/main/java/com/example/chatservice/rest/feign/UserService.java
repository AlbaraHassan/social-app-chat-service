package com.example.chatservice.rest.feign;

import com.example.chatservice.rest.dtos.UserDTO;
import com.example.chatservice.rest.models.UserModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@FeignClient(name = "USER-SERVICE", path = "/api/user")
public interface UserService {

  @GetMapping("/all")
  Optional<List<UserDTO>> getAll();

  @GetMapping
  Optional<UserDTO> get(@RequestParam String id);

  @GetMapping("/getMe")
  UserDTO getMe(@RequestHeader("Authorization") String authorizationHeader);

  @DeleteMapping
  boolean delete(@RequestParam String id);

  @PostMapping
  Optional<UserModel> create(@RequestBody UserModel data);

  @PostMapping("/verify")
  Optional<UserModel> verify(@RequestParam String id, @RequestBody UserModel data);

  @GetMapping("/email")
  Optional<UserModel> getByEmail(@RequestParam String email);

  @GetMapping("/search")
  List<UserModel> search(@RequestParam String userName);

  @GetMapping("/getByUsername")
  Optional<UserDTO> getByUsername(@RequestParam String userName);
}
