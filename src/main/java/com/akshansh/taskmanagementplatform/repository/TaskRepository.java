package com.akshansh.taskmanagementplatform.repository;

import com.akshansh.taskmanagementplatform.dto.response.TaskResponse;
import com.akshansh.taskmanagementplatform.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT new com.akshansh.taskmanagementplatform.dto.response.TaskResponse(" +
            "t.id, t.title, t.description, t.status, t.priority, t.createdAt, t.dueDate, t.assignee.name, " +
            "t.project.title)" +
            "FROM Task t WHERE t.assignee.id = ?1")
    List<TaskResponse> findAllByAssignee_Id(Long assigneeId);

    @Query("SELECT new com.akshansh.taskmanagementplatform.dto.response.TaskResponse(" +
            "t.id, t.title, t.description, t.status, t.priority, t.createdAt, t.dueDate, t.assignee.name, " +
            "t.project.title)" +
            "FROM Task t WHERE t.project.id = ?1")
    List<TaskResponse> findAllByProject_Id(Long projectId);

    @Query("SELECT new com.akshansh.taskmanagementplatform.dto.response.TaskResponse(" +
            "t.id, t.title, t.description, t.status, t.priority, t.createdAt, t.dueDate, t.assignee.name, " +
            "t.project.title)" +
            "FROM Task t")
    Page<TaskResponse> findAllTasks(Pageable pageable);
}
