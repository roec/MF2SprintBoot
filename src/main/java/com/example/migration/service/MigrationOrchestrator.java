package com.example.migration.service;

import com.example.migration.agent.SemanticIrBuilder;
import com.example.migration.dto.MigrationRequest;
import com.example.migration.dto.MigrationResponse;
import com.example.migration.ir.SemanticIr;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MigrationOrchestrator {

    private final SemanticIrBuilder semanticIrBuilder;
    private final CodeGenerationService codeGenerationService;

    public MigrationOrchestrator(SemanticIrBuilder semanticIrBuilder, CodeGenerationService codeGenerationService) {
        this.semanticIrBuilder = semanticIrBuilder;
        this.codeGenerationService = codeGenerationService;
    }

    public MigrationResponse migrate(MigrationRequest request) {
        SemanticIr ir = semanticIrBuilder.build(request);
        MigrationResponse.GeneratedProject project = codeGenerationService.generate(ir);

        List<String> risks = new ArrayList<>();
        if (ir.logic().steps().stream().noneMatch(step -> step.type().equals("WRITE"))) {
            risks.add("No write path detected; classified as inquiry-only migration.");
        }
        if (request.artifacts().size() < 2) {
            risks.add("Limited source context may reduce semantic reconstruction accuracy.");
        }

        return new MigrationResponse(ir, project, risks, ir.sourceTrace());
    }
}
