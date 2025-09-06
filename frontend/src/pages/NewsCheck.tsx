import { FormEvent, useState } from 'react';
import { checkNews, useApiAuth } from '../lib/api';

export default function NewsCheck() {
  const { token, username } = useApiAuth();
  const [form, setForm] = useState({ headline: '', author: '', sourceSite: '' });
  const [result, setResult] = useState<any | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setResult(null);
    try {
      const data = await checkNews(token, username, form);
      setResult(data);
    } catch (err: any) {
      setError(err?.message ?? 'Failed to check news');
    }
  }

  return (
    <div>
      <h2>Check News</h2>
      <form onSubmit={onSubmit} style={{ display: 'grid', gap: 12, maxWidth: 640 }}>
        <input placeholder="Headline" value={form.headline} onChange={(e) => setForm({ ...form, headline: e.target.value })} required />
        <input placeholder="Author" value={form.author} onChange={(e) => setForm({ ...form, author: e.target.value })} />
        <input placeholder="Source site (domain)" value={form.sourceSite} onChange={(e) => setForm({ ...form, sourceSite: e.target.value })} />
        <button type="submit">Verify</button>
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

