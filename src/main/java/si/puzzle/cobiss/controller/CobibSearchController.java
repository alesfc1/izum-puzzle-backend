package si.puzzle.cobiss.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import si.puzzle.cobiss.dto.ErrorDetail;
import si.puzzle.cobiss.dto.ErrorResponse;
import si.puzzle.cobiss.dto.SearchResponse;
import si.puzzle.cobiss.service.BookSearchService;

@RestController
@RequestMapping("/cobiss/api/si/sl/search/cobib")
public class CobibSearchController {

    private final BookSearchService bookSearchService;

    public CobibSearchController(BookSearchService bookSearchService) {
        this.bookSearchService = bookSearchService;
    }

    @GetMapping
    public SearchResponse search(
            @RequestParam("q") String query,
            @RequestParam(value = "prf", required = false) String profile,
            @RequestParam(value = "max", defaultValue = "500") int max
    ) {
        return bookSearchService.search(query, profile, max);
    }

    @GetMapping("/display/{id}")
    public ResponseEntity<?> getBook(@PathVariable String id) {
        return bookSearchService.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(
                        new ErrorResponse(new ErrorDetail("Zapis s tem ID-jem ni bil najden"))
                ));
    }
}
