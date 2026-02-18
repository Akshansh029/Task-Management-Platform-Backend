package com.akshansh.taskmanagementplatform.dto.response;

import com.akshansh.taskmanagementplatform.entity.Project;
import com.akshansh.taskmanagementplatform.entity.Task;
import com.akshansh.taskmanagementplatform.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

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
    private Long ownerId;
    private String ownerName;
    private String ownerEmail;
    private Set<TaskResponse> tasks;
    private Set<UserProfileResponse> members;

    public static ProjectDetailsResponse convertToDetailedDto(Project prj){
        return new ProjectDetailsResponse(
                prj.getId(),
                prj.getTitle(),
                prj.getDescription(),
                prj.getStartDate(),
                prj.getEndDate(),
                prj.getCreatedAt(),
                prj.getOwner().getId(),
                prj.getOwner().getName(),
                prj.getOwner().getEmail(),
                prj.getTasks().stream().map(TaskResponse::convertToDto).collect(Collectors.toSet()),
                prj.getMembers().stream().map(User::convertToDto).collect(Collectors.toSet())
        );
    }
}
