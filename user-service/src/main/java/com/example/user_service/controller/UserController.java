package com.example.user_service.controller;

import com.example.user_service.dto.request.AuthRequest;
import com.example.user_service.dto.request.UserRequest;
import com.example.user_service.dto.response.UserResponse;
import com.example.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @RequestMapping(value = "/validate", method = RequestMethod.POST)
    public Boolean validateUserCredentials(@RequestBody AuthRequest authRequest) {
        return userService.validateUserCredentials(authRequest);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Boolean registerUser(@RequestBody UserRequest userRequest) {
        return userService.registerUser(userRequest);
    }

    @RequestMapping(value = "/getUserDetails", method = RequestMethod.GET)
    public UserResponse getUserDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                       @RequestHeader(value = "X-Username", required = false) String username) {
        return userService.getUserDetails(username);
    }

    @RequestMapping(value = "/updateUserDetails", method = RequestMethod.PUT)
    public UserResponse updateUserDetails(@RequestHeader(value = "X-Username", required = false) String username,
                                          @RequestBody UserRequest userRequest) {
        if (!Objects.equals(username, userRequest.getUsername())) {
            return null;
        }
        return userService.updateUserDetails(userRequest);
    }

    @RequestMapping(value = "/updatePassword", method = RequestMethod.PUT)
    public Boolean updatePassword(@RequestHeader(value = "X-Username", required = false) String username,
                                  @RequestBody AuthRequest authRequest) {
        if (!Objects.equals(username, authRequest.getUsername())) {
            return null;
        }
        return userService.updatePassword(username, authRequest.getPassword());
    }

}
