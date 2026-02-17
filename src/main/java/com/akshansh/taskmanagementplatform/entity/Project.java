package com.akshansh.taskmanagementplatform.entity;

import com.akshansh.taskmanagementplatform.dto.response.ProjectResponse;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@Getter @Setter @NoArgsConstructor @ToString
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Column(name = "title", nullable = false)
    private String title;

    @Size(max = 1000, message = "Description can be max 1000 characters")
    @NotBlank(message = "Description is required")
    @Column(name = "description")
    private String description;

    @NotNull(message = "Start data cannot be null")
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToMany(mappedBy = "projects", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<User> members = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private Set<Task> tasks = new HashSet<>();


    public Project(String title, String description, LocalDateTime startDate, LocalDateTime endDate){
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = LocalDateTime.now();
    }

    public static ProjectResponse convertToDto(Project p){
        return new ProjectResponse(
                p.getId(),
                p.getTitle(),
                p.getStartDate(),
                p.getEndDate(),
                p.getCreatedAt(),
                p.owner.getName(),
                p.members.size(),
                p.tasks.size()
        );
    }

    public void addMember(User u){
        this.members.add(u);
        u.getProjects().add(this);
    }

    public void removeMember(User u){
        this.members.remove(u);
        u.getProjects().remove(this);
    }
}