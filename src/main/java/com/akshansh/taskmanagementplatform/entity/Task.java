package com.akshansh.taskmanagementplatform.entity;

import com.akshansh.taskmanagementplatform.dto.response.TaskResponse;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tasks")
@Getter @Setter @NoArgsConstructor @ToString
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description can be max 500 characters")
    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private TaskPriority priority;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @ToString.Exclude
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id", nullable = false)
    private User assignee;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "task_labels",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private Set<Label> labels = new HashSet<>();

    public Task(
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDateTime dueDate
    ){
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
        this.dueDate = dueDate;
    }

    public static TaskResponse convertToDto(Task t){
        return new TaskResponse(
          t.getTitle(),
          t.getDescription(),
          t.getStatus(),
          t.getPriority(),
          t.getCreatedAt(),
          t.getDueDate(),
          t.getAssignee().getName(),
          t.getProject().getTitle()
        );
    }
}
