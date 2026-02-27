package com.akshansh.taskmanagementplatform.exception;

public class InvalidEnumValueException extends IllegalArgumentException {
    public InvalidEnumValueException(String message) {
        super(message);
    }
}
