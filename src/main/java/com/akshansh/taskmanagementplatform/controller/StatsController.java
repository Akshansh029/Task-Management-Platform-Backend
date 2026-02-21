package com.akshansh.taskmanagementplatform.controller;

import com.akshansh.taskmanagementplatform.dto.response.StatsResponse;
import com.akshansh.taskmanagementplatform.service.StatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    private final StatsService statsService;

    public StatsController(StatsService statsService){
        this.statsService = statsService;
    }

    @GetMapping
    public ResponseEntity<StatsResponse> getStats(){
        StatsResponse stats = statsService.getStats();
        return ResponseEntity.status(HttpStatus.OK).body(stats);
    }
}
