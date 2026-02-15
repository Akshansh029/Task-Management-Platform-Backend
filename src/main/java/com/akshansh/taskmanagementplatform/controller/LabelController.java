package com.akshansh.taskmanagementplatform.controller;

import com.akshansh.taskmanagementplatform.dto.request.CreateLabelRequest;
import com.akshansh.taskmanagementplatform.dto.response.LabelResponse;
import com.akshansh.taskmanagementplatform.service.LabelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LabelController {
    private final LabelService labelService;

    public LabelController(LabelService labelService){
        this.labelService = labelService;
    }

    @GetMapping("/api/labels")
    public ResponseEntity<List<LabelResponse>> getAllLabels(){
        List<LabelResponse> labels = labelService.getAllLabels();
        return ResponseEntity.status(HttpStatus.OK).body(labels);
    }

    @PostMapping("/api/labels")
    public ResponseEntity<LabelResponse> createLabel(
            @Valid @RequestBody CreateLabelRequest request
    ){
        LabelResponse created = labelService.createLabel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/api/tasks/{taskId}/labels/{labelId}")
    public ResponseEntity<Void> addLabelToTask(
        @PathVariable Long taskId,
        @PathVariable Long labelId
    ){
        labelService.addLabelToTask(taskId, labelId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/api/tasks/{taskId}/labels/{labelId}")
    public ResponseEntity<Void> removeLabelFromTask(
        @PathVariable Long taskId,
        @PathVariable Long labelId
    ){
        labelService.removeLabelFromTask(taskId, labelId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/labels/{labelId}")
    public ResponseEntity<Void> deleteLabel(
        @PathVariable Long labelId
    ){
        labelService.deleteLabel(labelId);
        return ResponseEntity.noContent().build();
    }
}
