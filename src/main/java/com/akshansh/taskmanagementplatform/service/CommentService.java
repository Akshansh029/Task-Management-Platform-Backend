package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.dto.request.CreateCommentRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateCommentRequest;
import com.akshansh.taskmanagementplatform.dto.response.CommentResponse;
import com.akshansh.taskmanagementplatform.entity.*;
import com.akshansh.taskmanagementplatform.exception.ForbiddenException;
import com.akshansh.taskmanagementplatform.exception.ResourceNotFoundException;
import com.akshansh.taskmanagementplatform.repository.CommentRepository;
import com.akshansh.taskmanagementplatform.repository.ProjectRepository;
import com.akshansh.taskmanagementplatform.repository.TaskRepository;
import com.akshansh.taskmanagementplatform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.akshansh.taskmanagementplatform.dto.response.CommentResponse.convertToDto;

@Service
public class CommentService {
    private final CommentRepository commentRepo;
    private final TaskRepository taskRepo;
    private final UserRepository userRepo;
    private final ProjectRepository projectRepo;

    public CommentService(
            CommentRepository commentRepo,
            TaskRepository taskRepo,
            UserRepository userRepo,
            ProjectRepository projectRepo
    ){
        this.commentRepo = commentRepo;
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
        this.projectRepo = projectRepo;
    }

    public boolean isAdmin(Long userId){
        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User ID: " + userId + " not found"));

        return user.getRole() == UserRole.ADMIN;
    }

    @Transactional
    public CommentResponse createComment(Long taskId, CreateCommentRequest request) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task ID: " + taskId + " not found"));

        User author = userRepo.findById(request.getAuthorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User ID: " + request.getAuthorId() + " not found"));

        Project prj = projectRepo.findById(request.getProjectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project ID: " + request.getProjectId() + " not found"));

        // Check if author is project member
        if(!prj.getMembers().contains(author))
            throw new ForbiddenException("Only project members can comment on tasks");

        Comment newComment = new Comment(request.getContent(), author, task);
        commentRepo.save(newComment);
        return convertToDto(newComment);
    }

    public List<CommentResponse> getAllComments(Long taskId) {

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task ID: " + taskId + " not found"));

        return task.getComments().stream().map(CommentResponse::convertToDto).toList();
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, UpdateCommentRequest request) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Comment with ID: " + commentId + " not found"));

        User user = userRepo.findById(request.getUserId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("User with ID: " + request.getUserId() + " not found"));

        // Check if user and author are same
        if(!comment.getAuthor().equals(user))
            throw new ForbiddenException("Only comment author can edit comment");

        comment.setContent(request.getContent());
        commentRepo.save(comment);
        return convertToDto(comment);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Comment with ID: " + commentId + " not found"));

        User user = userRepo.findById(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User with ID: " + userId + " not found"));

        if(!comment.getAuthor().equals(user) || !isAdmin(userId))
            throw new ForbiddenException("Only admin and comment author can delete comment");

        commentRepo.deleteById(commentId);
    }
}
