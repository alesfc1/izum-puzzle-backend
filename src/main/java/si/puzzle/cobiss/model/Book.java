package si.puzzle.cobiss.model;

public record Book(
        long id,
        String title,
        String author,
        String coverUrl,
        String description
) {
}
