# AI Fake News Detector - Frontend

## Scripts

- **dev**: start Vite dev server on `http://localhost:5173`
- **build**: typecheck and build
- **preview**: preview production build

## Environment

Create a `.env` file with:

```
VITE_API_BASE=http://localhost:8080
VITE_CRED_BASE=http://localhost:8094
```

## Pages

- **Login**, **Register**
- **Check News**: POST `/api/news/check`
- **History**: GET `/api/news/getAllNews`, search by headline
- **Source Credibility**: POST `/credibility/evaluate`

## Auth

- Stores token and username in localStorage
- Sends `Authorization: Bearer <token>` and `X-Username: <username>` to news endpoints