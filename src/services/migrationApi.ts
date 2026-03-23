import { MigrationRequest, MigrationResponse } from '../types';

export const runMigration = async (payload: MigrationRequest): Promise<MigrationResponse> => {
  const response = await fetch('/api/migrate', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(payload)
  });

  if (!response.ok) {
    const body = await response.text();
    throw new Error(`Migration failed (${response.status}): ${body}`);
  }

  return (await response.json()) as MigrationResponse;
};
