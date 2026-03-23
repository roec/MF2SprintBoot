package com.example.migration.service;

import com.example.migration.agent.SemanticIrBuilder;
import com.example.migration.dto.MigrationRequest;
import com.example.migration.llm.DeepSeekClient;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MigrationOrchestratorTest {

    @Test
    void shouldBuildIrAndGenerateProjectInDryRunMode() {
        DeepSeekClient deepSeekClient = new DeepSeekClient(
                RestClient.builder().build(),
                new com.example.migration.config.DeepSeekProperties("https://api.deepseek.com/v1", "", "deepseek-chat")
        );
        MigrationOrchestrator orchestrator = new MigrationOrchestrator(
                new SemanticIrBuilder(),
                new CodeGenerationService(deepSeekClient)
        );

        MigrationRequest request = new MigrationRequest(
                "CUSTINQ",
                "INQUIRY",
                List.of(
                        new MigrationRequest.SourceArtifact("RPG", "CUSTINQ", "CHAIN CUSTPF; IF STATUS = 'A'; ENDIF;"),
                        new MigrationRequest.SourceArtifact("PF", "CUSTPF", "A CUST_NO 9P 0")
                ),
                List.of("status A is active")
        );

        var response = orchestrator.migrate(request);

        assertNotNull(response.semanticIr());
        assertNotNull(response.generatedProject());
        assertFalse(response.generatedProject().controllerCode().isBlank());
    }
}
