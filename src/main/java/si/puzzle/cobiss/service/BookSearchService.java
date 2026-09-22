package si.puzzle.cobiss.service;

import org.springframework.stereotype.Service;
import si.puzzle.cobiss.client.CobissPlusClient;
import si.puzzle.cobiss.config.CobissProperties;
import si.puzzle.cobiss.dto.BookDetailResponse;
import si.puzzle.cobiss.dto.SearchItem;
import si.puzzle.cobiss.dto.SearchResponse;
import si.puzzle.cobiss.dto.SearchValue;
import si.puzzle.cobiss.model.Book;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class BookSearchService {

    private static final Pattern DIACRITICAL_MARKS = Pattern.compile("\\p{M}+");

    private final CobissProperties properties;
    private final CobissPlusClient cobissPlusClient;
    private final CoverUrlResolver coverUrlResolver;

    public BookSearchService(
            CobissProperties properties,
            CobissPlusClient cobissPlusClient,
            CoverUrlResolver coverUrlResolver
    ) {
        this.properties = properties;
        this.cobissPlusClient = cobissPlusClient;
        this.coverUrlResolver = coverUrlResolver;
    }

    public SearchResponse search(String query, String profile, int max) {
        if (properties.isMock()) {
            return searchMock(query, max);
        }
        return cobissPlusClient.search(query, profile, max);
    }

    public Optional<BookDetailResponse> findById(String id) {
        if (properties.isMock()) {
            return findMockById(id);
        }
        return cobissPlusClient.findById(id);
    }

    private SearchResponse searchMock(String query, int max) {
        String normalizedQuery = normalize(query);

        List<SearchItem> items = MockBookData.BOOKS.stream()
                .filter(book -> matches(book, normalizedQuery))
                .sorted(Comparator.comparing(Book::title))
                .limit(Math.max(max, 0))
                .map(this::toSearchItem)
                .toList();

        return new SearchResponse(new SearchValue(items.size(), items));
    }

    private Optional<BookDetailResponse> findMockById(String id) {
        return MockBookData.BOOKS.stream()
                .filter(book -> String.valueOf(book.id()).equals(id))
                .findFirst()
                .map(this::toDetailResponse);
    }

    private boolean matches(Book book, String query) {
        if (query.isBlank()) {
            return true;
        }

        return normalize(book.title()).contains(query)
                || normalize(book.author()).contains(query)
                || String.valueOf(book.id()).contains(query);
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        return DIACRITICAL_MARKS.matcher(normalized)
                .replaceAll("")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim();
    }

    private SearchItem toSearchItem(Book book) {
        String coverUrl = book.coverUrl() != null
                ? book.coverUrl()
                : coverUrlResolver.recordPageUrl(String.valueOf(book.id()));

        return new SearchItem(book.id(), book.title(), book.author(), coverUrl);
    }

    private BookDetailResponse toDetailResponse(Book book) {
        String coverUrl = book.coverUrl() != null
                ? book.coverUrl()
                : coverUrlResolver.recordPageUrl(String.valueOf(book.id()));

        return new BookDetailResponse(
                String.valueOf(book.id()),
                book.title(),
                book.author(),
                coverUrl,
                book.description()
        );
    }
}
