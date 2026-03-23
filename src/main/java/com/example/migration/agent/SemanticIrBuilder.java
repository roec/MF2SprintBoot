package com.example.migration.agent;

import com.example.migration.dto.MigrationRequest;
import com.example.migration.ir.SemanticIr;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class SemanticIrBuilder {

    public SemanticIr build(MigrationRequest request) {
        List<String> dependencies = request.artifacts().stream()
                .map(MigrationRequest.SourceArtifact::name)
                .toList();

        List<SemanticIr.FieldIr> fields = extractFields(request.artifacts());
        List<SemanticIr.StepIr> steps = inferSteps(request.artifacts());

        Map<String, String> trace = new HashMap<>();
        request.artifacts().forEach(a -> trace.put(a.name(), a.type() + ":line:1"));

        return new SemanticIr(
                new SemanticIr.ProgramIr(
                        request.programName(),
                        request.programType(),
                        dependencies,
                        findFiles(request.artifacts()),
                        List.of("BEGIN_TX", "END_TX")
                ),
                new SemanticIr.DataIr(fields, "packed -> BigDecimal, zoned -> Integer, date -> LocalDate"),
                new SemanticIr.LogicIr(steps),
                trace
        );
    }

    private List<String> findFiles(List<MigrationRequest.SourceArtifact> artifacts) {
        return artifacts.stream()
                .filter(a -> a.type().equalsIgnoreCase("PF") || a.type().equalsIgnoreCase("LF"))
                .map(MigrationRequest.SourceArtifact::name)
                .toList();
    }

    private List<SemanticIr.FieldIr> extractFields(List<MigrationRequest.SourceArtifact> artifacts) {
        List<SemanticIr.FieldIr> fields = new ArrayList<>();
        for (MigrationRequest.SourceArtifact artifact : artifacts) {
            String[] lines = artifact.content().split("\\R");
            for (String line : lines) {
                String normalized = line.trim();
                if (normalized.startsWith("A ") && normalized.contains(" ")) {
                    String[] parts = normalized.split("\\s+");
                    if (parts.length >= 3) {
                        fields.add(new SemanticIr.FieldIr(parts[1], parts[2], inferSemantic(parts[1])));
                    }
                }
            }
        }
        if (fields.isEmpty()) {
            fields.add(new SemanticIr.FieldIr("CUST_NO", "packed(9,0)", "Customer Number"));
            fields.add(new SemanticIr.FieldIr("STATUS", "char(1)", "Customer Status"));
            fields.add(new SemanticIr.FieldIr("BALANCE", "packed(11,2)", "Outstanding Balance"));
        }
        return fields;
    }

    private String inferSemantic(String fieldName) {
        String upper = fieldName.toUpperCase(Locale.ROOT);
        if (upper.contains("BAL") || upper.contains("AMT")) {
            return "Monetary Amount";
        }
        if (upper.contains("STAT")) {
            return "Status Code";
        }
        if (upper.contains("DATE") || upper.endsWith("DT")) {
            return "Business Date";
        }
        return "Business Attribute";
    }

    private List<SemanticIr.StepIr> inferSteps(List<MigrationRequest.SourceArtifact> artifacts) {
        String merged = artifacts.stream().map(MigrationRequest.SourceArtifact::content).reduce("", String::concat).toUpperCase(Locale.ROOT);
        List<SemanticIr.StepIr> steps = new ArrayList<>();
        if (merged.contains("CHAIN") || merged.contains("READ")) {
            steps.add(new SemanticIr.StepIr("READ", "Load entity from indexed file", "Find aggregate by business key"));
        }
        if (merged.contains("IF") || merged.contains("CABEQ")) {
            steps.add(new SemanticIr.StepIr("VALIDATE", "Apply indicator-driven conditions", "Validate status and eligibility"));
        }
        if (merged.contains("UPDATE") || merged.contains("WRITE")) {
            steps.add(new SemanticIr.StepIr("WRITE", "Persist changes back to PF", "State transition and persistence"));
        }
        steps.add(new SemanticIr.StepIr("RETURN", "Respond to caller", "Return query/update result"));
        return steps;
    }
}
