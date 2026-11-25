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
    void ranksMatchingDocumentsByTfIdfForSingleTerm() {
        assertEquals(List.of("doc2", "doc1"), SearchEngine.search(documents, "shoot"));
    }

    @Test
    void keepsInputOrderForEqualTfIdfScores() {
        var rankedDocuments =
                List.of(
                        Map.of("id", "once", "text", "pint!"),
                        Map.of("id", "z-first", "text", "PINT! pint."),
                        Map.of("id", "a-second", "text", "pint pint!"),
                        Map.of("id", "no-match", "text", "pints pints pints"));

        assertEquals(
                List.of("once", "z-first", "a-second"),
                SearchEngine.search(rankedDocuments, "PINT!"));
    }

    @Test
    void findsDocumentsMatchingAnyQueryTerm() {
        assertEquals(List.of("doc2", "doc1"), SearchEngine.search(documents, "shoot at me"));
    }

    @Test
    void ignoresQueryTermsAbsentFromEveryDocument() {
        assertEquals(List.of("doc2", "doc1"), SearchEngine.search(documents, "shoot at me, nerd"));
        assertEquals(List.of(), SearchEngine.search(documents, "nerd absent"));
    }

    @Test
    void normalizesPunctuationAndCaseInMultiwordQueries() {
        assertEquals(List.of("doc2", "doc1"), SearchEngine.search(documents, "SHOOT! AT, ME."));
    }

    @Test
    void sumsTfIdfAcrossDistinctQueryTerms() {
        var rankedDocuments =
                List.of(
                        Map.of("id", "one", "text", "shoot shoot shoot shoot shoot"),
                        Map.of("id", "two", "text", "shoot at"),
                        Map.of("id", "three", "text", "shoot at at"));

        var expected = List.of("three", "two", "one");
        assertEquals(expected, SearchEngine.search(rankedDocuments, "shoot at"));
        assertEquals(expected, SearchEngine.search(rankedDocuments, "shoot at SHOOT!"));
    }

    @Test
    void ranksAnArticleAboveRepetitiveSpam() {
        var rankedDocuments =
                List.of(
                        Map.of("id", "spam", "text", "trash trash trash trash"),
                        Map.of("id", "article", "text", "trash island"));

        var result = SearchEngine.search(rankedDocuments, "trash island");
        assertEquals(List.of("article", "spam"), result);
    }

    @Test
    void normalizesTermFrequencyByDocumentLength() {
        var rankedDocuments =
                List.of(
                        Map.of("id", "long", "text", "trash trash a b c d"),
                        Map.of("id", "short", "text", "trash island ! —"));

        assertEquals(List.of("short", "long"), SearchEngine.search(rankedDocuments, "trash"));
    }

    @Test
    void aRareTermCanOutweighTwoCommonTerms() {
        var rankedDocuments =
                List.of(
                        Map.of("id", "first", "text", "trash island"),
                        Map.of("id", "second", "text", "trash island"),
                        Map.of("id", "rare", "text", "ocean"));

        var result = SearchEngine.search(rankedDocuments, "trash island ocean");
        assertEquals(List.of("rare", "first", "second"), result);
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
        assertEquals(List.of("doc"), SearchEngine.search(multilineDocuments, "first item_42"));
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
