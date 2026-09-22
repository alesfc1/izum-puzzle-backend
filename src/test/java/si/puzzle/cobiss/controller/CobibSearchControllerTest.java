package si.puzzle.cobiss.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CobibSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void searchReturnsMatchingBooks() throws Exception {
        mockMvc.perform(get("/cobiss/api/si/sl/search/cobib")
                        .param("q", "harry")
                        .param("prf", "cobiss ela")
                        .param("max", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value.hitsNo").value(3))
                .andExpect(jsonPath("$.value.searchItems", hasSize(3)))
                .andExpect(jsonPath("$.value.searchItems[0].primary").value("Harry Potter. Dvorana skrivnosti"));
    }

    @Test
    void searchIgnoresDiacritics() throws Exception {
        mockMvc.perform(get("/cobiss/api/si/sl/search/cobib")
                        .param("q", "najmlajse")
                        .param("max", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value.hitsNo").value(1))
                .andExpect(jsonPath("$.value.searchItems[0].primary").value("Mali princ za najmlajše"))
                .andExpect(jsonPath("$.value.searchItems[0].coverUrl").value("https://d.cobiss.net/repository/si/thumbnails/290172928"));
    }

    @Test
    void searchReturnsEmptyResultsForUnknownQuery() throws Exception {
        mockMvc.perform(get("/cobiss/api/si/sl/search/cobib")
                        .param("q", "nonexistent-book-xyz")
                        .param("max", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value.hitsNo").value(0))
                .andExpect(jsonPath("$.value.searchItems", hasSize(0)));
    }

    @Test
    void displayReturnsBookDetails() throws Exception {
        mockMvc.perform(get("/cobiss/api/si/sl/search/cobib/display/69922307"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("69922307"))
                .andExpect(jsonPath("$.primary").value("Harry Potter. Kamen modrosti"))
                .andExpect(jsonPath("$.coverUrl").value("https://d.cobiss.net/repository/si/thumbnails/69922307"))
                .andExpect(jsonPath("$.addon02").exists());
    }

    @Test
    void displayReturnsErrorForUnknownId() throws Exception {
        mockMvc.perform(get("/cobiss/api/si/sl/search/cobib/display/999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error.text").value("Zapis s tem ID-jem ni bil najden"));
    }
}
