package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.dto.request.CreateCommentRequest;
import com.akshansh.taskmanagementplatform.dto.response.CommentResponse;
import com.akshansh.taskmanagementplatform.repository.CommentRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private final CommentRepository commentRepo;

    public CommentService(CommentRepository commentRepo){
        this.commentRepo = commentRepo;
    }


    public CommentResponse createComment(Long taskId, CreateCommentRequest request) {

    }
}
