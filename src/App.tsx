import { useState } from 'react';
import ArtifactEditor from './components/ArtifactEditor';
import { runMigration } from './services/migrationApi';
import { MigrationResponse, SourceArtifact } from './types';

const emptyArtifact = (): SourceArtifact => ({ type: 'RPG', name: '', content: '' });

export default function App() {
  const [programName, setProgramName] = useState('CUSTINQ');
  const [programType, setProgramType] = useState('INQUIRY');
  const [rules, setRules] = useState('status A means active');
  const [artifacts, setArtifacts] = useState<SourceArtifact[]>([
    { type: 'RPG', name: 'CUSTINQ', content: 'CHAIN CUSTPF; IF STATUS = \"A\"; ENDIF;' },
    { type: 'PF', name: 'CUSTPF', content: 'A CUST_NO 9P 0\nA STATUS 1A\nA BALANCE 11P 2' }
  ]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [result, setResult] = useState<MigrationResponse | null>(null);

  const updateArtifact = (index: number, artifact: SourceArtifact) => {
    const next = [...artifacts];
    next[index] = artifact;
    setArtifacts(next);
  };

  const removeArtifact = (index: number) => {
    setArtifacts(artifacts.filter((_, i) => i !== index));
  };

  const addArtifact = () => {
    setArtifacts([...artifacts, emptyArtifact()]);
  };

  const submit = async () => {
    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const data = await runMigration({
        programName,
        programType,
        artifacts,
        optionalRules: rules.split(/\r?\n/).filter(Boolean)
      });
      setResult(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="page">
      <h1>Semantic Migration Studio (React + TypeScript + DeepSeek)</h1>

      <section className="panel">
        <div className="row">
          <input value={programName} onChange={(e) => setProgramName(e.target.value)} placeholder="Program Name" />
          <select value={programType} onChange={(e) => setProgramType(e.target.value)}>
            <option value="INQUIRY">INQUIRY</option>
            <option value="UPDATE">UPDATE</option>
            <option value="BATCH">BATCH</option>
          </select>
        </div>
        <textarea
          rows={2}
          value={rules}
          onChange={(e) => setRules(e.target.value)}
          placeholder="Optional business rules, one per line"
        />
      </section>

      <section className="panel">
        <h2>Source Artifacts</h2>
        {artifacts.map((artifact, index) => (
          <ArtifactEditor
            key={`${index}-${artifact.name}`}
            artifact={artifact}
            onChange={(next) => updateArtifact(index, next)}
            onRemove={() => removeArtifact(index)}
          />
        ))}
        <button type="button" onClick={addArtifact}>
          + Add Artifact
        </button>
      </section>

      <section className="panel">
        <button type="button" onClick={submit} disabled={loading}>
          {loading ? 'Migrating...' : 'Run Semantic Migration'}
        </button>
        {error && <p className="error">{error}</p>}
      </section>

      {result && (
        <section className="panel result-grid">
          <article>
            <h3>Semantic IR</h3>
            <pre>{JSON.stringify(result.semanticIr, null, 2)}</pre>
          </article>
          <article>
            <h3>Generated Controller</h3>
            <pre>{result.generated.controller}</pre>
            <h3>Generated Service</h3>
            <pre>{result.generated.service}</pre>
            <h3>OpenAPI Snippet</h3>
            <pre>{result.generated.openApi}</pre>
          </article>
          <article>
            <h3>Risks</h3>
            <ul>
              {result.risks.map((risk) => (
                <li key={risk}>{risk}</li>
              ))}
            </ul>
          </article>
        </section>
      )}
    </main>
  );
}
