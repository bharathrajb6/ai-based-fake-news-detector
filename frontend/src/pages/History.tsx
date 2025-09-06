import { useEffect, useState } from 'react';
import { getByHeadline, getHistory, useApiAuth } from '../lib/api';

export default function History() {
  const { token, username } = useApiAuth();
  const [items, setItems] = useState<any[]>([]);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [query, setQuery] = useState('');
  const [error, setError] = useState<string | null>(null);

  async function load() {
    setError(null);
    try {
      const data = await getHistory(token, username, page, size);
      setItems(data.content ?? []);
      setTotalPages(data.totalPages ?? 0);
    } catch (err: any) {
      setError(err?.message ?? 'Failed to load history');
    }
  }

  useEffect(() => { load(); /* eslint-disable-next-line react-hooks/exhaustive-deps */ }, [page]);

  async function search() {
    if (!query) return load();
    setError(null);
    try {
      const data = await getByHeadline(token, username, query);
      setItems(data ? [data] : []);
      setTotalPages(1);
      setPage(0);
    } catch (err: any) {
      setError(err?.message ?? 'Search failed');
    }
  }

  return (
    <div>
      <h2>History</h2>
      <div style={{ display: 'flex', gap: 8, marginBottom: 12 }}>
        <input placeholder="Search by headline" value={query} onChange={(e) => setQuery(e.target.value)} />
        <button onClick={search}>Search</button>
        <button onClick={() => { setQuery(''); load(); }}>Reset</button>
      </div>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <table cellPadding={8} style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr>
            <th align="left">Headline</th>
            <th align="left">Author</th>
            <th align="left">Result</th>
            <th align="left">Credibility</th>
            <th align="left">Source</th>
            <th align="left">Trust</th>
          </tr>
        </thead>
        <tbody>
          {items.map((it) => (
            <tr key={it.id}>
              <td>{it.headline}</td>
              <td>{it.author}</td>
              <td>{it.result}</td>
              <td>{it.credibilityScore}</td>
              <td>{it.sourceSite}</td>
              <td>{it.sourceTrustScore}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <div style={{ marginTop: 12, display: 'flex', gap: 8, alignItems: 'center' }}>
        <button disabled={page <= 0} onClick={() => setPage((p) => Math.max(0, p - 1))}>Prev</button>
        <span>Page {page + 1} / {Math.max(1, totalPages)}</span>
        <button disabled={page + 1 >= totalPages} onClick={() => setPage((p) => p + 1)}>Next</button>
      </div>
    </div>
  );
}

