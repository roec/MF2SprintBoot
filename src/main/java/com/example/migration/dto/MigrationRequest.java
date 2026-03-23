package com.example.migration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record MigrationRequest(
        @NotBlank String programName,
        @NotBlank String programType,
        @NotEmpty List<SourceArtifact> artifacts,
        List<String> optionalRules
) {
    public record SourceArtifact(
            @NotBlank String type,
            @NotBlank String name,
            @NotBlank String content
    ) {
    }
}
