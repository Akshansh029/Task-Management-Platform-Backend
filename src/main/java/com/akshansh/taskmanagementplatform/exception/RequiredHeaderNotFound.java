package com.akshansh.taskmanagementplatform.exception;

public class RequiredHeaderNotFound extends RuntimeException{
    public RequiredHeaderNotFound(String msg){
        super(msg);
    }
}
