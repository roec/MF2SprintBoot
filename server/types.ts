export type SourceArtifact = {
  type: 'RPG' | 'RPGLE' | 'CL' | 'PF' | 'LF' | 'DDS' | 'DDL' | 'DSPF' | string;
  name: string;
  content: string;
};

export type MigrationRequest = {
  programName: string;
  programType: 'INQUIRY' | 'UPDATE' | 'BATCH' | string;
  artifacts: SourceArtifact[];
  optionalRules?: string[];
};

export type SemanticIr = {
  program: {
    programName: string;
    type: string;
    dependencies: string[];
    files: string[];
  };
  data: {
    fields: Array<{ name: string; type: string; semanticMeaning: string }>;
    mappingSuggestion: string;
  };
  logic: {
    steps: Array<{ type: string; description: string; semanticIntent: string }>;
  };
  sourceTrace: Record<string, string>;
};
