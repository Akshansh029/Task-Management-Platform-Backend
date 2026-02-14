package com.akshansh.taskmanagementplatform.exception;

public class UserNotPartOfProjectException extends RuntimeException{
    public UserNotPartOfProjectException(String msg){
        super(msg);
    }
}
