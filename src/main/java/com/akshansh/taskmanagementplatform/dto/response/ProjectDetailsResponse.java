package com.akshansh.taskmanagementplatform.dto.response;

import com.akshansh.taskmanagementplatform.entity.Project;
import com.akshansh.taskmanagementplatform.entity.Task;
import com.akshansh.taskmanagementplatform.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDetailsResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private String ownerName;
    private String ownerEmail;
    private Set<Task> tasks;
    private Set<User> members;

    public static ProjectDetailsResponse convertToDetailedDto(Project prj){
        return new ProjectDetailsResponse(
                prj.getId(),
                prj.getTitle(),
                prj.getDescription(),
                prj.getStartDate(),
                prj.getEndDate(),
                prj.getCreatedAt(),
                prj.getOwner().getName(),
                prj.getOwner().getEmail(),
                prj.getTasks(),
                prj.getMembers()
        );
    }
}
