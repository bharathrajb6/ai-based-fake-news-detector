import { Routes, Route, Navigate, Link } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import NewsCheck from './pages/NewsCheck';
import History from './pages/History';
import Credibility from './pages/Credibility';
import { AuthProvider, useAuth } from './auth/AuthContext';

function ProtectedRoute({ children }: { children: JSX.Element }) {
  const { token } = useAuth();
  return token ? children : <Navigate to="/login" replace />;
}

function Layout({ children }: { children: React.ReactNode }) {
  const { token, logout } = useAuth();
  return (
    <div style={{ maxWidth: 960, margin: '0 auto', padding: 16 }}>
      <header style={{ display: 'flex', gap: 12, alignItems: 'center', justifyContent: 'space-between' }}>
        <nav style={{ display: 'flex', gap: 12 }}>
          <Link to="/">Check News</Link>
          <Link to="/history">History</Link>
          <Link to="/credibility">Source Credibility</Link>
        </nav>
        <div>
          {token ? (
            <button onClick={logout}>Logout</button>
          ) : (
            <>
              <Link to="/login">Login</Link>
              <span style={{ margin: '0 6px' }}>|</span>
              <Link to="/register">Register</Link>
            </>
          )}
        </div>
      </header>
      <main style={{ marginTop: 24 }}>{children}</main>
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <Layout>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <NewsCheck />
              </ProtectedRoute>
            }
          />
          <Route
            path="/history"
            element={
              <ProtectedRoute>
                <History />
              </ProtectedRoute>
            }
          />
          <Route
            path="/credibility"
            element={
              <ProtectedRoute>
                <Credibility />
              </ProtectedRoute>
            }
          />
        </Routes>
      </Layout>
    </AuthProvider>
  );
}

