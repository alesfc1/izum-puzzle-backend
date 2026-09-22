package si.puzzle.cobiss.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import si.puzzle.cobiss.config.CobissProperties;

@Component
public class CoverUrlResolver {

    private final CobissProperties properties;

    public CoverUrlResolver(CobissProperties properties) {
        this.properties = properties;
    }

    /**
     * Returns the COBISS Plus record page for a bibliographic ID.
     * Cover images are associated with records at this URL.
     */
    public String recordPageUrl(String id) {
        return properties.recordPageUrl(id);
    }

    /**
     * Extracts coverUrl from a COBISS API JSON node, checking common field names.
     */
    public String extractCoverUrl(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }

        String direct = textOrNull(node.get("coverUrl"));
        if (direct != null) {
            return direct;
        }

        JsonNode cover = node.get("cover");
        if (cover != null && cover.isObject()) {
            String url = textOrNull(cover.get("url"));
            if (url != null) {
                return url;
            }
        }

        return null;
    }

    private String textOrNull(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        String value = node.asText(null);
        return value == null || value.isBlank() ? null : value;
    }
}
