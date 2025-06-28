package com.rpg.exceptions;

public class DatabaseException extends GameException {
    public DatabaseException(String message) {
        super("Database error occurred. Please try again.", message);
    }

    public DatabaseException(String message, Throwable cause) {
        super("Database error occurred. Please try again.", message, cause);
    }
}