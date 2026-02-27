package com.akshansh.taskmanagementplatform.exception;

public class WrongPasswordException extends RuntimeException{
    public WrongPasswordException(String msg){
        super(msg);
    }
}
