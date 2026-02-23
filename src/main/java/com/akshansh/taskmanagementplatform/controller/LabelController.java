package com.akshansh.taskmanagementplatform.controller;

import com.akshansh.taskmanagementplatform.dto.request.CreateLabelRequest;
import com.akshansh.taskmanagementplatform.dto.response.LabelResponse;
import com.akshansh.taskmanagementplatform.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Label Management", description = "APIs for managing labels")
public class LabelController {
    private final LabelService labelService;

    public LabelController(LabelService labelService){
        this.labelService = labelService;
    }


    @Operation(summary = "Get all labels", description = "Retrieve a list of all labels in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Labels retrieved successfully",
                    content = @Content(schema = @Schema(implementation = LabelResponse.class)))
    })
    @GetMapping("/api/labels")
    public ResponseEntity<List<LabelResponse>> getAllLabels(){
        List<LabelResponse> labels = labelService.getAllLabels();
        return ResponseEntity.status(HttpStatus.OK).body(labels);
    }

    @Operation(summary = "Create a new label", description = "Add a new label to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label created successfully",
                    content = @Content(schema = @Schema(implementation = LabelResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/api/labels")
    public ResponseEntity<LabelResponse> createLabel(
            @Valid @RequestBody CreateLabelRequest request
    ){
        LabelResponse created = labelService.createLabel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @Operation(summary = "Add a label to task", description = "Add a label to an existing task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label added successfully"),
            @ApiResponse(responseCode = "404", description = "Label/Task not found",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/api/tasks/{taskId}/labels/{labelId}")
    public ResponseEntity<Void> addLabelToTask(
        @PathVariable Long taskId,
        @PathVariable Long labelId
    ){
        labelService.addLabelToTask(taskId, labelId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @Operation(summary = "Remove a label to task", description = "Remove a label from a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label removed successfully"),
            @ApiResponse(responseCode = "404", description = "Label/Task not found",
                    content = @Content(schema = @Schema()))
    })
    @DeleteMapping("/api/tasks/{taskId}/labels/{labelId}")
    public ResponseEntity<Void> removeLabelFromTask(
        @PathVariable Long taskId,
        @PathVariable Long labelId
    ){
        labelService.removeLabelFromTask(taskId, labelId);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Delete a label", description = "Delete a label from the system using its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Label deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Label not found",
                    content = @Content(schema = @Schema()))
    })
    @DeleteMapping("/api/labels/{labelId}")
    public ResponseEntity<Void> deleteLabel(
        @PathVariable Long labelId
    ){
        labelService.deleteLabel(labelId);
        return ResponseEntity.noContent().build();
    }
}
