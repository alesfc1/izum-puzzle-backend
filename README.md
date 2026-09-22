# COBISS Puzzle Backend

Drop-in replacement for the IZUM CPlus backend used by the puzzle game frontend.

Implements the same REST contract on port **8080**, so the existing Next.js proxy routes work without changes.

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/cobiss/api/si/sl/search/cobib?q={query}&prf=cobiss ela&max=500` | Search books |
| GET | `/cobiss/api/si/sl/search/cobib/display/{id}` | Get book details |

## Requirements

- Java 21+
- Maven 3.9+

## Run

```bash
cd cobiss-puzzle-backend
mvn spring-boot:run
```

The server starts at `http://localhost:8080`.

## Run with frontend

Terminal 1 — backend:

```bash
cd cobiss-puzzle-backend
mvn spring-boot:run
```

Terminal 2 — frontend:

```bash
cd izum-puzzle-produkcija/puzzle-game
npm install
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) and search for books like `harry`, `tolkien`, or `orwell`.

## Mock data

The service ships with 8 sample books in `BookSearchService`. Replace this with a real COBISS data source when available.

Cover URLs currently use the `d.cobiss.net` domain so they match the frontend `next.config.ts` image allowlist. Replace them with real COBISS cover URLs when you connect to live data.

## Next steps

1. Replace `BookSearchService` mock list with a database or COBISS integration
2. Add caching for search results
3. Contact IZUM (`podpora@izum.si`) if you need access to the real CobissPlus-API / CLib services
