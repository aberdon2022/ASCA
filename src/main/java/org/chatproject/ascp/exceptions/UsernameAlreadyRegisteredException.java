package org.chatproject.ascp.exceptions;

public class UsernameAlreadyRegisteredException extends RuntimeException {
    public UsernameAlreadyRegisteredException(String message) {
        super(message);
    }
}
