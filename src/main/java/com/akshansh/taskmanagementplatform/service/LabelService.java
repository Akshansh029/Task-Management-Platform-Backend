package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.dto.request.CreateLabelRequest;
import com.akshansh.taskmanagementplatform.dto.response.LabelResponse;
import com.akshansh.taskmanagementplatform.entity.Label;
import com.akshansh.taskmanagementplatform.entity.Task;
import com.akshansh.taskmanagementplatform.exception.ResourceNotFoundException;
import com.akshansh.taskmanagementplatform.repository.LabelRepository;
import com.akshansh.taskmanagementplatform.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LabelService {
    private final LabelRepository labelRepo;
    private final TaskRepository taskRepo;

    public LabelService(LabelRepository labelRepo, TaskRepository taskRepo){
        this.labelRepo = labelRepo;
        this.taskRepo = taskRepo;
    }

    @Transactional
    public LabelResponse createLabel(CreateLabelRequest request) {
        Label label = new Label(request.getName(), request.getColor());
        labelRepo.save(label);

        return LabelResponse.convertToDto(label);
    }

    public List<LabelResponse> getAllLabels() {
        return labelRepo.findAll().stream().map(LabelResponse::convertToDto).toList();
    }

    @Transactional
    public void addLabelToTask(Long taskId, Long labelId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID: " + taskId + " not found"));

        Label label = labelRepo.findById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException("Label with ID: " + labelId + " not found"));

        label.addLabelToTask(task);
    }

    @Transactional
    public void removeLabelFromTask(Long taskId, Long labelId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID: " + taskId + " not found"));

        Label label = labelRepo.findById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException("Label with ID: " + labelId + " not found"));

        label.removeLabelFromTask(task);
    }

    @Transactional
    public void deleteLabel(Long labelId) {
        labelRepo.deleteById(labelId);
    }
}
