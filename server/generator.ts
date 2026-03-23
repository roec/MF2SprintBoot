import { SemanticIr } from './types.js';

export const generateSpringBootCode = (ir: SemanticIr, llmHints: string) => {
  const baseName = ir.program.programName.toLowerCase();
  const className = ir.program.programName.charAt(0).toUpperCase() + ir.program.programName.slice(1).toLowerCase();

  return {
    entity: `@Entity\npublic class ${className}Entity {\n  @Id private Long id;\n  private String status;\n  private BigDecimal balance;\n}`,
    repository: `public interface ${className}Repository extends JpaRepository<${className}Entity, Long> {}`,
    service: `@Service\npublic class ${className}Service {\n  public String execute(String id) { return \"semantic-use-case\"; }\n}`,
    controller: `@RestController\n@RequestMapping(\"/api/${baseName}\")\npublic class ${className}Controller {\n  @GetMapping(\"/{id}\")\n  public String query(@PathVariable String id) { return id; }\n}`,
    openApi: `paths:\n  /api/${baseName}/{id}:\n    get:\n      summary: Semantic use-case endpoint\n      description: ${llmHints.replace(/\n/g, ' ')}`
  };
};
