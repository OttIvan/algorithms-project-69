package hexlet.code;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SearchEngineTest {
    private final List<Map<String, String>> documents =
            List.of(
                    Map.of("id", "doc1", "text", "I can't shoot straight unless I've had a pint!"),
                    Map.of("id", "doc2", "text", "Don't shoot shoot shoot that thing at me."),
                    Map.of("id", "doc3", "text", "I'm your shooter."));

    @Test
    void returnsEachMatchingIdOnceInInputOrder() {
        assertEquals(List.of("doc1", "doc2"), SearchEngine.search(documents, "shoot"));
    }

    @Test
    void keepsPunctuationAttachedToWords() {
        assertEquals(List.of(), SearchEngine.search(documents, "pint"));
        assertEquals(List.of("doc1"), SearchEngine.search(documents, "pint!"));
    }

    @Test
    void comparesWordsCaseSensitively() {
        assertEquals(List.of(), SearchEngine.search(documents, "Shoot"));
    }

    @Test
    void handlesSeveralSpacesBetweenWords() {
        var spacedDocuments = List.of(Map.of("id", "doc", "text", "  first  shoot   last  "));

        assertEquals(List.of("doc"), SearchEngine.search(spacedDocuments, "shoot"));
    }

    @Test
    void returnsAnEmptyListForAnEmptyCollection() {
        assertEquals(List.of(), SearchEngine.search(List.of(), "shoot"));
    }

    @Test
    void returnsAnEmptyListForAnEmptyQuery() {
        var emptyDocument = List.of(Map.of("id", "empty", "text", ""));

        assertEquals(List.of(), SearchEngine.search(emptyDocument, ""));
    }
}
