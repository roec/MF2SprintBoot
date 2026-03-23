export type SourceArtifact = {
  type: string;
  name: string;
  content: string;
};

export type MigrationRequest = {
  programName: string;
  programType: string;
  artifacts: SourceArtifact[];
  optionalRules: string[];
};

export type MigrationResponse = {
  semanticIr: unknown;
  generated: {
    entity: string;
    repository: string;
    service: string;
    controller: string;
    openApi: string;
  };
  risks: string[];
};
