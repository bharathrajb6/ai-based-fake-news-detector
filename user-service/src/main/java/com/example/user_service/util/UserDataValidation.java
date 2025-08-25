package com.example.user_service.util;

import com.example.user_service.dto.UserRequest;

public class UserDataValidation {
    public static boolean isValidFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            return false;
        }
        char firstChar = firstName.trim().charAt(0);
        if (Character.isDigit(firstChar)) {
            return false;
        }
        return true;
    }

    public static boolean isValidLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            return false;
        }
        char firstChar = lastName.trim().charAt(0);
        if (Character.isDigit(firstChar)) {
            return false;
        }
        return true;
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Basic email regex
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public static boolean isValidUsername(String username) {
        return username != null && !username.trim().isEmpty();
    }

    public static boolean isValidPassword(String password) {
        return password != null && !password.trim().isEmpty();
    }

    public static boolean isValidContactNumber(String contactNumber) {
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            return false;
        }
        // Basic phone number check: digits only, length 7-15
        return contactNumber.matches("^\\d{7,15}$");
    }

    /**
     * Validates all attributes of UserRequest
     */
    public static boolean validateUserRequest(UserRequest userRequest) {
        return isValidFirstName(userRequest.getFirstName()) &&
               isValidLastName(userRequest.getLastName()) &&
               isValidEmail(userRequest.getEmail()) &&
               isValidUsername(userRequest.getUsername()) &&
               isValidPassword(userRequest.getPassword()) &&
               isValidContactNumber(userRequest.getContactNumber());
    }
}
