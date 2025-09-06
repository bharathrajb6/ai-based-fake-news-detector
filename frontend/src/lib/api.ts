import { useAuth } from '../auth/AuthContext';

export const API_GATEWAY_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080';
export const CREDIBILITY_BASE = import.meta.env.VITE_CRED_BASE ?? 'http://localhost:8094';

export function getAuthHeaders(token: string | null, username: string | null) {
  const headers: Record<string, string> = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = `Bearer ${token}`;
  if (username) headers['X-Username'] = username;
  return headers;
}

export async function loginRequest(username: string, password: string) {
  const res = await fetch(`${API_GATEWAY_BASE}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  if (!res.ok) throw new Error(await res.text());
  const data = await res.json() as { token: string };
  return data.token;
}

export async function registerRequest(payload: {
  firstName: string; lastName: string; email: string; username: string; password: string; contactNumber: string;
}) {
  const res = await fetch(`${API_GATEWAY_BASE}/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  if (!res.ok) throw new Error(await res.text());
  const data = await res.json() as { token: string };
  return data.token;
}

export async function checkNews(token: string | null, username: string | null, payload: {
  headline: string; author: string; sourceSite: string;
}) {
  const res = await fetch(`${API_GATEWAY_BASE}/api/news/check`, {
    method: 'POST',
    headers: getAuthHeaders(token, username),
    body: JSON.stringify(payload)
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

export async function getHistory(token: string | null, username: string | null, page = 0, size = 10) {
  const res = await fetch(`${API_GATEWAY_BASE}/api/news/getAllNews?page=${page}&size=${size}`, {
    headers: getAuthHeaders(token, username)
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

export async function getByHeadline(token: string | null, username: string | null, headline: string) {
  const res = await fetch(`${API_GATEWAY_BASE}/api/news/getNewsByHeadline?headline=${encodeURIComponent(headline)}`, {
    headers: getAuthHeaders(token, username)
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

export async function evaluateSource(payload: { site: string; headline?: string; username?: string }) {
  const res = await fetch(`${CREDIBILITY_BASE}/credibility/evaluate`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

export function useApiAuth() {
  const { token, username } = useAuth();
  return { token, username };
}

