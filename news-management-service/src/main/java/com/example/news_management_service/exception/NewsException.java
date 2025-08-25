package com.example.news_management_service.exception;

public class NewsException extends RuntimeException {

    public NewsException(String message) {
        super(message);
    }


    public NewsException(String message, Throwable t) {
        super(message, t);
    }
}
