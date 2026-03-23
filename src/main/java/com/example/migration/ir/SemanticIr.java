package com.example.migration.ir;

import java.util.List;
import java.util.Map;

public record SemanticIr(
        ProgramIr program,
        DataIr data,
        LogicIr logic,
        Map<String, String> sourceTrace
) {
    public record ProgramIr(
            String programName,
            String type,
            List<String> dependencies,
            List<String> files,
            List<String> commitPoints
    ) {
    }

    public record DataIr(
            List<FieldIr> fields,
            String mappingSuggestion
    ) {
    }

    public record FieldIr(
            String name,
            String type,
            String semanticMeaning
    ) {
    }

    public record LogicIr(
            List<StepIr> steps
    ) {
    }

    public record StepIr(
            String type,
            String description,
            String semanticIntent
    ) {
    }
}
