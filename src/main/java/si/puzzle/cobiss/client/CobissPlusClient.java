package si.puzzle.cobiss.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;
import si.puzzle.cobiss.config.CobissProperties;
import si.puzzle.cobiss.dto.BookDetailResponse;
import si.puzzle.cobiss.dto.SearchItem;
import si.puzzle.cobiss.dto.SearchResponse;
import si.puzzle.cobiss.dto.SearchValue;
import si.puzzle.cobiss.service.CoverUrlResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CobissPlusClient {

    private static final Logger log = LoggerFactory.getLogger(CobissPlusClient.class);

    private final RestClient restClient;
    private final CobissProperties properties;
    private final CoverUrlResolver coverUrlResolver;
    private final ObjectMapper objectMapper;

    public CobissPlusClient(
            RestClient cobissRestClient,
            CobissProperties properties,
            CoverUrlResolver coverUrlResolver,
            ObjectMapper objectMapper
    ) {
        this.restClient = cobissRestClient;
        this.properties = properties;
        this.coverUrlResolver = coverUrlResolver;
        this.objectMapper = objectMapper;
    }

    public SearchResponse search(String query, String profile, int max) {
        String uri = UriComponentsBuilder
                .fromPath("/cobiss/api/si/sl/search/cobib")
                .queryParam("q", query)
                .queryParam("prf", profile != null ? profile : "cobiss ela")
                .queryParam("max", max)
                .build()
                .toUriString();

        JsonNode response = fetchJson(uri);
        return mapSearchResponse(response);
    }

    public Optional<BookDetailResponse> findById(String id) {
        String uri = "/cobiss/api/si/sl/search/cobib/display/" + id;

        JsonNode response = fetchJson(uri);
        if (response == null) {
            return Optional.empty();
        }

        if (response.has("error")) {
            return Optional.empty();
        }

        return Optional.of(mapBookDetail(response, id));
    }

    private JsonNode fetchJson(String uri) {
        try {
            return restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException ex) {
            log.error("COBISS Plus request failed for {}: {}", uri, ex.getMessage());
            return null;
        }
    }

    private SearchResponse mapSearchResponse(JsonNode response) {
        if (response == null || !response.has("value")) {
            return new SearchResponse(new SearchValue(0, List.of()));
        }

        JsonNode value = response.get("value");
        JsonNode itemsNode = value.get("searchItems");
        List<SearchItem> items = new ArrayList<>();

        if (itemsNode != null && itemsNode.isArray()) {
            for (JsonNode itemNode : itemsNode) {
                SearchItem item = mapSearchItem(itemNode);
                if (item != null) {
                    items.add(item);
                }
            }
        }

        int hitsNo = value.has("hitsNo") ? value.get("hitsNo").asInt(items.size()) : items.size();
        return new SearchResponse(new SearchValue(hitsNo, items));
    }

    private SearchItem mapSearchItem(JsonNode itemNode) {
        Long id = itemNode.has("id") ? itemNode.get("id").asLong() : null;
        String primary = textOrNull(itemNode.get("primary"));
        String secondary = textOrNull(itemNode.get("secondary"));
        String coverUrl = coverUrlResolver.extractCoverUrl(itemNode);

        if (coverUrl == null && id != null) {
            coverUrl = fetchCoverUrlFromDisplay(String.valueOf(id));
        }

        return new SearchItem(id, primary, secondary, coverUrl);
    }

    private BookDetailResponse mapBookDetail(JsonNode node, String id) {
        String coverUrl = coverUrlResolver.extractCoverUrl(node);
        if (coverUrl == null) {
            coverUrl = fetchCoverUrlFromDisplay(id);
        }

        return new BookDetailResponse(
                node.has("id") ? node.get("id").asText() : id,
                textOrNull(node.get("primary")),
                textOrNull(node.get("secondary")),
                coverUrl,
                textOrNull(node.get("addon02"))
        );
    }

    /**
     * Fetches coverUrl from the display endpoint for a record.
     * Record page: https://plus.cobiss.net/cobiss/si/sl/search/cobib/{id}
     */
    private String fetchCoverUrlFromDisplay(String id) {
        String uri = "/cobiss/api/si/sl/search/cobib/display/" + id;
        JsonNode detail = fetchJson(uri);
        if (detail == null || detail.has("error")) {
            log.debug("No cover found for record {} ({})", id, coverUrlResolver.recordPageUrl(id));
            return null;
        }
        return coverUrlResolver.extractCoverUrl(detail);
    }

    private String textOrNull(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        String value = node.asText(null);
        return value == null || value.isBlank() ? null : value;
    }
}
