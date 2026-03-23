import { MigrationRequest, SemanticIr } from './types.js';

const meaningByField = (field: string): string => {
  const upper = field.toUpperCase();
  if (upper.includes('BAL') || upper.includes('AMT')) return 'Monetary Amount';
  if (upper.includes('STAT')) return 'Lifecycle Status';
  if (upper.includes('DATE') || upper.endsWith('DT')) return 'Business Date';
  return 'Business Attribute';
};

export const buildSemanticIr = (request: MigrationRequest): SemanticIr => {
  const merged = request.artifacts.map((a) => a.content.toUpperCase()).join('\n');

  const files = request.artifacts
    .filter((a) => ['PF', 'LF', 'DDS', 'DDL'].includes(a.type.toUpperCase()))
    .map((a) => a.name);

  const fields = request.artifacts
    .flatMap((a) => a.content.split(/\r?\n/).map((line) => line.trim()))
    .filter((line) => line.startsWith('A '))
    .map((line) => line.split(/\s+/))
    .filter((parts) => parts.length >= 3)
    .map((parts) => ({
      name: parts[1],
      type: parts[2],
      semanticMeaning: meaningByField(parts[1])
    }));

  const safeFields =
    fields.length > 0
      ? fields
      : [
          { name: 'CUST_NO', type: 'packed(9,0)', semanticMeaning: 'Customer ID' },
          { name: 'STATUS', type: 'char(1)', semanticMeaning: 'Lifecycle Status' },
          { name: 'BALANCE', type: 'packed(11,2)', semanticMeaning: 'Monetary Amount' }
        ];

  const steps = [] as SemanticIr['logic']['steps'];
  if (merged.includes('CHAIN') || merged.includes('READ')) {
    steps.push({
      type: 'READ',
      description: 'Load customer aggregate from indexed file.',
      semanticIntent: 'Find Customer By ID Use Case'
    });
  }
  if (merged.includes('IF') || merged.includes('CABEQ')) {
    steps.push({
      type: 'VALIDATE',
      description: 'Evaluate indicator/status rules.',
      semanticIntent: 'Check customer eligibility'
    });
  }
  if (merged.includes('UPDATE') || merged.includes('WRITE')) {
    steps.push({
      type: 'WRITE',
      description: 'Persist updated state to PF.',
      semanticIntent: 'Apply status transition and save'
    });
  }
  steps.push({
    type: 'RETURN',
    description: 'Build response payload.',
    semanticIntent: 'Return use-case output'
  });

  return {
    program: {
      programName: request.programName,
      type: request.programType,
      dependencies: request.artifacts.map((a) => a.name),
      files
    },
    data: {
      fields: safeFields,
      mappingSuggestion: 'packed -> BigDecimal, zoned -> Integer, date -> LocalDate'
    },
    logic: {
      steps
    },
    sourceTrace: Object.fromEntries(request.artifacts.map((a) => [a.name, `${a.type}:line:1`]))
  };
};
