import { SourceArtifact } from '../types';

type Props = {
  artifact: SourceArtifact;
  onChange: (artifact: SourceArtifact) => void;
  onRemove: () => void;
};

export default function ArtifactEditor({ artifact, onChange, onRemove }: Props) {
  return (
    <div className="artifact-card">
      <div className="row">
        <input
          value={artifact.type}
          placeholder="Type (RPG/PF/LF...)"
          onChange={(e) => onChange({ ...artifact, type: e.target.value })}
        />
        <input
          value={artifact.name}
          placeholder="Artifact Name"
          onChange={(e) => onChange({ ...artifact, name: e.target.value })}
        />
        <button type="button" onClick={onRemove}>
          Remove
        </button>
      </div>
      <textarea
        value={artifact.content}
        placeholder="Paste RPG / DDS / CL content here"
        rows={6}
        onChange={(e) => onChange({ ...artifact, content: e.target.value })}
      />
    </div>
  );
}
