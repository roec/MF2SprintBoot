package com.example.migration.controller;

import com.example.migration.dto.MigrationRequest;
import com.example.migration.dto.MigrationResponse;
import com.example.migration.service.MigrationOrchestrator;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/migrations")
public class MigrationController {

    private final MigrationOrchestrator orchestrator;

    public MigrationController(MigrationOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/convert")
    public MigrationResponse convert(@Valid @RequestBody MigrationRequest request) {
        return orchestrator.migrate(request);
    }
}
