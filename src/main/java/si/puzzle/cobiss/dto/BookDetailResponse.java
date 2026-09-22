package si.puzzle.cobiss.dto;

public record BookDetailResponse(
        String id,
        String primary,
        String secondary,
        String coverUrl,
        String addon02
) {
}
