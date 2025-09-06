import { FormEvent, useState } from 'react';
import { evaluateSource } from '../lib/api';
import { useAuth } from '../auth/AuthContext';

export default function Credibility() {
  const { username } = useAuth();
  const [form, setForm] = useState({ site: '', headline: '' });
  const [result, setResult] = useState<any | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setResult(null);
    try {
      const data = await evaluateSource({ site: form.site, headline: form.headline, username: username ?? undefined });
      setResult(data);
    } catch (err: any) {
      setError(err?.message ?? 'Evaluation failed');
    }
  }

  return (
    <div>
      <h2>Source Credibility</h2>
      <form onSubmit={onSubmit} style={{ display: 'grid', gap: 12, maxWidth: 640 }}>
        <input placeholder="Site (domain)" value={form.site} onChange={(e) => setForm({ ...form, site: e.target.value })} required />
        <input placeholder="Headline (optional)" value={form.headline} onChange={(e) => setForm({ ...form, headline: e.target.value })} />
        <button type="submit">Evaluate</button>
      </form>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      {result && (
        <div style={{ marginTop: 16 }}>
          <h3>Result</h3>
          <pre style={{ background: '#f5f5f5', padding: 12, overflowX: 'auto' }}>{JSON.stringify(result, null, 2)}</pre>
        </div>
      )}
    </div>
  );
}

