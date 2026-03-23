import 'dotenv/config';
import cors from 'cors';
import express from 'express';
import { DeepSeekClient } from './deepseekClient.js';
import { generateSpringBootCode } from './generator.js';
import { buildSemanticIr } from './semanticEngine.js';
import { MigrationRequest } from './types.js';

const app = express();
app.use(cors());
app.use(express.json({ limit: '2mb' }));

const deepseek = new DeepSeekClient(process.env.DEEPSEEK_API_KEY, process.env.DEEPSEEK_MODEL, process.env.DEEPSEEK_BASE_URL);

app.get('/api/health', (_req, res) => {
  res.json({ ok: true, at: new Date().toISOString() });
});

app.post('/api/migrate', async (req, res) => {
  const body = req.body as MigrationRequest;
  if (!body?.programName || !body?.artifacts?.length) {
    return res.status(400).json({ message: 'programName and artifacts are required.' });
  }

  try {
    const ir = buildSemanticIr(body);
    const llmHints = await deepseek.complete(
      `Based on this Semantic IR, produce migration guidance and caveats for Spring Boot: ${JSON.stringify(ir)}`
    );

    const generated = generateSpringBootCode(ir, llmHints);
    return res.json({
      semanticIr: ir,
      generated,
      risks: [
        ir.logic.steps.some((s) => s.type === 'WRITE')
          ? 'Write path detected: ensure transactional boundaries.'
          : 'No write path detected: inquiry-only program.',
        'Auto-generated output should be reviewed for business semantics.'
      ]
    });
  } catch (error) {
    const message = error instanceof Error ? error.message : 'Unknown migration error';
    return res.status(500).json({ message });
  }
});

const port = Number(process.env.PORT ?? 8787);
app.listen(port, () => {
  console.log(`Semantic migration API listening on http://localhost:${port}`);
});
