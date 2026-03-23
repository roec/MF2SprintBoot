package com.example.migration.service;

import com.example.migration.dto.MigrationResponse;
import com.example.migration.ir.SemanticIr;
import com.example.migration.llm.DeepSeekClient;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class CodeGenerationService {

    private final DeepSeekClient deepSeekClient;

    public CodeGenerationService(DeepSeekClient deepSeekClient) {
        this.deepSeekClient = deepSeekClient;
    }

    public MigrationResponse.GeneratedProject generate(SemanticIr ir) {
        String entityName = toClassName(ir.program().programName()) + "Entity";
        String packageName = "com.generated." + ir.program().programName().toLowerCase(Locale.ROOT);

        String prompt = "Generate concise Spring Boot snippets for: " + ir;
        String llmHints = deepSeekClient.complete(prompt);

        String entity = "package " + packageName + ";\n\n"
                + "import jakarta.persistence.*;\n"
                + "import java.math.BigDecimal;\n\n"
                + "@Entity\npublic class " + entityName + " {\n"
                + "  @Id private Long id;\n"
                + "  private String status;\n"
                + "  private BigDecimal balance;\n"
                + "}\n";

        String repository = "package " + packageName + ";\n\n"
                + "import org.springframework.data.jpa.repository.JpaRepository;\n\n"
                + "public interface " + entityName + "Repository extends JpaRepository<" + entityName + ", Long> {}\n";

        String service = "package " + packageName + ";\n\n"
                + "import org.springframework.stereotype.Service;\n\n"
                + "@Service\npublic class " + toClassName(ir.program().programName()) + "Service {\n"
                + "  public String execute(String id){ return \"semantic-use-case\"; }\n"
                + "}\n";

        String controller = "package " + packageName + ";\n\n"
                + "import org.springframework.web.bind.annotation.*;\n\n"
                + "@RestController\n@RequestMapping(\"/api/" + ir.program().programName().toLowerCase(Locale.ROOT) + "\")\n"
                + "public class " + toClassName(ir.program().programName()) + "Controller {\n"
                + "  @GetMapping(\"/{id}\")\n  public String query(@PathVariable String id){ return id; }\n"
                + "}\n";

        String openApi = "paths:\n  /api/" + ir.program().programName().toLowerCase(Locale.ROOT)
                + "/{id}:\n    get:\n      summary: Semantic inquiry use-case\n      description: "
                + llmHints.replace("\n", " ");

        return new MigrationResponse.GeneratedProject(packageName, entity, repository, service, controller, openApi);
    }

    private String toClassName(String raw) {
        String cleaned = raw.replaceAll("[^a-zA-Z0-9]", " ");
        StringBuilder sb = new StringBuilder();
        for (String part : cleaned.split("\\s+")) {
            if (!part.isBlank()) {
                sb.append(part.substring(0, 1).toUpperCase(Locale.ROOT));
                sb.append(part.substring(1).toLowerCase(Locale.ROOT));
            }
        }
        if (sb.isEmpty()) {
            return "MigratedProgram";
        }
        return sb.toString();
    }
}
