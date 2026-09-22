package si.puzzle.cobiss.dto;

import java.util.List;

public record SearchValue(
        int hitsNo,
        List<SearchItem> searchItems
) {
}
