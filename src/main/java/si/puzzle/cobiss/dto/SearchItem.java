package si.puzzle.cobiss.dto;

public record SearchItem(
        Long id,
        String primary,
        String secondary,
        String coverUrl
) {
}
