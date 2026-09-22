# IZUM Cobiss Plus Puzzle Backend

Spring Boot backend for the puzzle game that simulates the REST contract of the existing IZUM Cobiss Plus backend. It uses local mock data by default, so the project can be run without access to the external COBISS API.

## Requirements

- Java 21 or newer
- Maven 3.9 or newer

## Run

From the project root, run:

```bash
mvn spring-boot:run
```

The server is available at `http://localhost:8080`.

## API

| Method | Path | Description |
|--------|------|-------------|
| GET | `/cobiss/api/si/sl/search/cobib?q={query}&prf={profile}&max={count}` | Search books |
| GET | `/cobiss/api/si/sl/search/cobib/display/{id}` | Get book details |

Examples:

```bash
curl "http://localhost:8080/cobiss/api/si/sl/search/cobib?q=tolkien&max=10"
curl "http://localhost:8080/cobiss/api/si/sl/search/cobib/display/1"
```

The `prf` parameter is optional, and `max` defaults to `500`.

## Mock and COBISS modes

The default configuration in `src/main/resources/application.properties` is:

```properties
cobiss.mock=true
```

Mock mode searches the built-in sample books. To use COBISS Plus, set:

```properties
cobiss.mock=false
```

The configured URLs for the external source are `cobiss.api-base-url` and `cobiss.record-base-url`. When connecting to a live source, verify that they match the available API and its terms of use.

## Tests

Run the tests with:

```bash
mvn test
```

## Run with the frontend

Run the backend in one terminal and the frontend in another:

```bash
cd ../izum-puzzle-produkcija/puzzle-game
npm install
npm run dev
```

The frontend is then available at `http://localhost:3000`.
