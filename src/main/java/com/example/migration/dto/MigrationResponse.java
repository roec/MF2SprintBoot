package com.example.migration.dto;

import com.example.migration.ir.SemanticIr;

import java.util.List;
import java.util.Map;

public record MigrationResponse(
        SemanticIr semanticIr,
        GeneratedProject generatedProject,
        List<String> risks,
        Map<String, String> traces
) {
    public record GeneratedProject(
            String packageName,
            String entityCode,
            String repositoryCode,
            String serviceCode,
            String controllerCode,
            String openApiSnippet
    ) {
    }
}
