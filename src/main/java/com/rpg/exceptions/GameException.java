package com.rpg.exceptions;

public class GameException extends Exception {
    private final String userMessage;
    private final String technicalMessage;

    public GameException(String message) {
        super(message);
        this.userMessage = message;
        this.technicalMessage = message;
    }

    public GameException(String userMessage, String technicalMessage) {
        super(technicalMessage);
        this.userMessage = userMessage;
        this.technicalMessage = technicalMessage;
    }

    public GameException(String userMessage, String technicalMessage, Throwable cause) {
        super(technicalMessage, cause);
        this.userMessage = userMessage;
        this.technicalMessage = technicalMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getTechnicalMessage() {
        return technicalMessage;
    }
}