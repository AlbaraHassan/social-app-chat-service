package com.example.chatservice.core.auth;



import com.example.chatservice.core.context.UserContext;
import com.example.chatservice.core.exceptions.GeneralException;
import com.example.chatservice.core.exceptions.auth.ForbiddenException;
import com.example.chatservice.core.exceptions.auth.UnauthorizedException;
import com.example.chatservice.core.helpers.JwtService;
import com.example.chatservice.rest.dtos.UserDTO;
import com.example.chatservice.rest.enums.Role;
import com.example.chatservice.rest.feign.UserService;
import io.jsonwebtoken.Claims;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Aspect
@Component
public class AuthAspect {

  private final UserService userService;
  private final UserContext userContext;
  private final JwtService jwtService;

  public AuthAspect(UserService userService, UserContext userContext, JwtService jwtService) {
    this.userService = userService;
    this.userContext = userContext;
    this.jwtService = jwtService;
  }

  @Before("@annotation(com.example.chatservice.core.auth.Auth)")
  public void authenticate(JoinPoint joinPoint) {
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
      .getRequestAttributes();
    MethodSignature ms = (MethodSignature) joinPoint.getSignature();
    Role[] expectedRoles = ms.getMethod().getAnnotation(Auth.class).value();

    if (requestAttributes != null) {
      String authorizationHeader = requestAttributes.getRequest().getHeader("Authorization");

      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        String token = authorizationHeader.substring(7);
        try {
          Claims claims = this.jwtService.decode(token);
          Date expirationDate = claims.getExpiration();
          Date now = new Date();

          if (expirationDate != null && expirationDate.before(now)) {
            throw new UnauthorizedException("Token has expired");
          }

          Optional<UserDTO> user = this.userService.get((String) claims.get("id"));

          if (user.isEmpty()) throw new UnauthorizedException("Invalid Token");

          if (user.get().getValidationCode() != null)
            throw new ForbiddenException("Account not verified");

          if (expectedRoles.length > 0 && !this.userHasRequiredRoles(user.get(), Arrays.asList(expectedRoles))) {
            throw new ForbiddenException("User does not have the required role");
          }

          this.userContext.setUser(user.get());
        } catch (GeneralException e) {
          throw e;
        }
      } else {
        throw new UnauthorizedException("Missing Bearer token");
      }
    } else {
      throw new UnauthorizedException("No request context available");
    }
  }

  private boolean userHasRequiredRoles(UserDTO user, List<Role> requiredRoles) {
    return requiredRoles.contains(user.getRole());
  }
}
