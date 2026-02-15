package com.akshansh.taskmanagementplatform.controller;

import com.akshansh.taskmanagementplatform.dto.request.CreateCommentRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateCommentRequest;
import com.akshansh.taskmanagementplatform.dto.response.CommentResponse;
import com.akshansh.taskmanagementplatform.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }

    @PostMapping("/api/tasks/{taskId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateCommentRequest request
    ){
        CommentResponse created = commentService.createComment(taskId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/api/tasks/{taskId}/comments")
    public ResponseEntity<List<CommentResponse>> getAllComments(
            @PathVariable Long taskId
    ){
        List<CommentResponse> comments = commentService.getAllComments(taskId);
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

    @PutMapping("/api/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest request
    ){
        CommentResponse updated = commentService.updateComment(commentId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @RequestHeader Long userId,
            @PathVariable Long commentId
    ){
        commentService.deleteComment(userId, commentId);
        return ResponseEntity.noContent().build();
    }
}
