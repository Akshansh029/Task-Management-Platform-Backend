package com.akshansh.taskmanagementplatform.controller;

import com.akshansh.taskmanagementplatform.dto.response.StatsResponse;
import com.akshansh.taskmanagementplatform.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
@Tag(name = "Stats Controller", description = "APIs for platform stats")
public class StatsController {
    private final StatsService statsService;

    public StatsController(StatsService statsService){
        this.statsService = statsService;
    }

    @Operation(summary = "Get all stats", description = "Retrieve stats for task manager platform")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stats retrieved successfully",
                    content = @Content(schema = @Schema(implementation = StatsResponse.class)))
    })
    @GetMapping
    public ResponseEntity<StatsResponse> getStats(){
        StatsResponse stats = statsService.getStats();
        return ResponseEntity.status(HttpStatus.OK).body(stats);
    }
}
