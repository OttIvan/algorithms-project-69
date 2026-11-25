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
    void stripsPunctuationInDocumentsAndQueries() {
        assertEquals(List.of("doc1"), SearchEngine.search(documents, "pint"));
        assertEquals(List.of("doc1"), SearchEngine.search(documents, "pint!"));
    }

    @Test
    void ignoresCaseInDocumentsAndQueries() {
        var uppercaseDocuments = List.of(Map.of("id", "doc", "text", "TRASH"));

        assertEquals(List.of("doc1"), SearchEngine.search(documents, "PINT"));
        assertEquals(List.of("doc"), SearchEngine.search(uppercaseDocuments, "trash"));
    }

    @Test
    void joinsPartsOfWordsWithApostrophes() {
        assertEquals(List.of("doc1"), SearchEngine.search(documents, "can't"));
        assertEquals(List.of("doc1"), SearchEngine.search(documents, "cant"));
        assertEquals(List.of(), SearchEngine.search(documents, "can"));
        assertEquals(List.of(), SearchEngine.search(documents, "t"));
    }

    @Test
    void joinsPartsOfHyphenatedWords() {
        var hyphenatedDocuments = List.of(Map.of("id", "doc", "text", "alpha-beta"));

        assertEquals(List.of("doc"), SearchEngine.search(hyphenatedDocuments, "alphabeta"));
        assertEquals(List.of("doc"), SearchEngine.search(hyphenatedDocuments, "alpha-beta"));
        assertEquals(List.of(), SearchEngine.search(hyphenatedDocuments, "alpha"));
        assertEquals(List.of(), SearchEngine.search(hyphenatedDocuments, "beta"));
    }

    @Test
    void ignoresPunctuationOnlyTokens() {
        var punctuatedDocuments = List.of(Map.of("id", "doc", "text", "— !!! pint!"));

        assertEquals(List.of(), SearchEngine.search(punctuatedDocuments, "—"));
        assertEquals(List.of(), SearchEngine.search(punctuatedDocuments, "!!!"));
        assertEquals(List.of("doc"), SearchEngine.search(punctuatedDocuments, "— PINT"));
    }

    @Test
    void keepsDigitsAndUnderscoresAndSplitsOnNewlines() {
        var multilineDocuments = List.of(Map.of("id", "doc", "text", "first\nItem_42!"));

        assertEquals(List.of("doc"), SearchEngine.search(multilineDocuments, "item_42"));
        assertEquals(List.of(), SearchEngine.search(multilineDocuments, "item42"));
        assertEquals(List.of(), SearchEngine.search(multilineDocuments, "first item_42"));
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
