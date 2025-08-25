package com.example.user_service.service;

import com.example.user_service.dto.AuthRequest;
import com.example.user_service.dto.UserRequest;
import com.example.user_service.dto.UserResponse;
import com.example.user_service.exceptions.UserException;
import com.example.user_service.mapper.UserMapper;
import com.example.user_service.model.User;
import com.example.user_service.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.user_service.util.UserDataValidation.validateUserRequest;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public Boolean validateUserCredentials(AuthRequest authRequest) {
        Optional<User> user = userRepo.findByUsername(authRequest.getUsername());
        if (!user.isPresent()) {
            throw new UserException("Username not found");
        }
        return passwordEncoder.matches(authRequest.getPassword(), user.get().getPassword());
    }


    public Boolean registerUser(UserRequest user) {
        if (!validateUserRequest(user)) {
            throw new UserException("User Data's are not correct");
        }
        Optional<User> existingUser = userRepo.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new UserException("Already username is exist.");
        }
        User newUser = userMapper.toUser(user);
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            userRepo.save(newUser);
        } catch (Exception exception) {
            throw new UserException("Unable to save the user details");
        }
        return true;
    }

    public UserResponse getUserDetails(String username) {
        Optional<User> user = userRepo.findByUsername(username);
        if (user.isPresent()) {
            UserResponse response = userMapper.toUserResponse(user.get());
            return response;
        }
        throw new UserException("User not found with this username");
    }

    /**
     * Update user details (firstName, lastName, email, contactNumber) for the given username.
     */
    public UserResponse updateUserDetails(UserRequest userRequest) {
        if (!validateUserRequest(userRequest)) {
            throw new UserException("User Data's are not correct");
        }
        Optional<User> userOpt = userRepo.findByUsername(userRequest.getUsername());
        if (userOpt.isPresent()) {
            try {
                userRepo.updateUserDetailsByUsername(userRequest.getUsername(), userRequest.getFirstName(), userRequest.getLastName(), userRequest.getEmail(), userRequest.getContactNumber());
            } catch (Exception exception) {
                throw new UserException("Unable to update");
            }
            return getUserDetails(userRequest.getUsername());
        }
        throw new UserException("User not found with this username");
    }

    /**
     * Update password for the given username.
     */
    public Boolean updatePassword(String username, String newPassword) {
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isPresent()) {
            try {
                userRepo.updateUserPasswordByUsername(username, passwordEncoder.encode(newPassword));
                return true;
            } catch (Exception exception) {
                throw new UserException("Unable to update user password");
            }
        }
        throw new UserException("User not found with this username");
    }
}
