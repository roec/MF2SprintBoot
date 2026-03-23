export class DeepSeekClient {
  constructor(
    private readonly apiKey: string | undefined,
    private readonly model = 'deepseek-chat',
    private readonly baseUrl = 'https://api.deepseek.com/v1'
  ) {}

  async complete(prompt: string): Promise<string> {
    if (!this.apiKey) {
      return '[DRY-RUN] No DEEPSEEK_API_KEY configured. Returning deterministic scaffold.';
    }

    const response = await fetch(`${this.baseUrl}/chat/completions`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${this.apiKey}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        model: this.model,
        temperature: 0.1,
        messages: [
          { role: 'system', content: 'You are an enterprise semantic migration architect.' },
          { role: 'user', content: prompt }
        ]
      })
    });

    if (!response.ok) {
      const text = await response.text();
      throw new Error(`DeepSeek API error ${response.status}: ${text}`);
    }

    const payload = (await response.json()) as {
      choices?: Array<{ message?: { content?: string } }>;
    };

    return payload.choices?.[0]?.message?.content ?? '[EMPTY] No completion returned by DeepSeek.';
  }
}
