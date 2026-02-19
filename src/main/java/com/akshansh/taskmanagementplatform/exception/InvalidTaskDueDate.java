package com.akshansh.taskmanagementplatform.exception;

public class InvalidTaskDueDate extends RuntimeException{
    public InvalidTaskDueDate(String msg){
        super(msg);
    }
}
