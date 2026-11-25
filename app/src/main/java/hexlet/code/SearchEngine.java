package hexlet.code;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchEngine {
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern NON_WORD = Pattern.compile("\\W+");

    public static List<String> search(List<Map<String, String>> documents, String query) {
        var queryTerms = new HashSet<>(termsOf(query));
        if (queryTerms.isEmpty()) {
            return List.of();
        }

        var scores = relevanceByDocument(buildIndex(documents), queryTerms);
        var matches = new ArrayList<>(scores.keySet());
        var relevanceOrder =
                Comparator.comparingInt(Relevance::matchedTerms)
                        .thenComparingInt(Relevance::occurrences);
        Comparator<IndexedDocument> byRelevance = Comparator.comparing(scores::get, relevanceOrder);
        matches.sort(byRelevance.reversed().thenComparingInt(IndexedDocument::position));
        return matches.stream().map(IndexedDocument::id).collect(Collectors.toList());
    }

    private static Map<String, Map<IndexedDocument, Integer>> buildIndex(
            List<Map<String, String>> documents) {
        var index = new HashMap<String, Map<IndexedDocument, Integer>>();
        var position = 0;
        for (var document : documents) {
            var indexedDocument = new IndexedDocument(document.get("id"), position++);
            for (var term : termsOf(document.get("text"))) {
                var postings = index.computeIfAbsent(term, key -> new HashMap<>());
                postings.merge(indexedDocument, 1, Integer::sum);
            }
        }
        return index;
    }

    private static Map<IndexedDocument, Relevance> relevanceByDocument(
            Map<String, Map<IndexedDocument, Integer>> index, Set<String> queryTerms) {
        var scores = new HashMap<IndexedDocument, Relevance>();
        for (var term : queryTerms) {
            for (var posting : index.getOrDefault(term, Map.of()).entrySet()) {
                var termScore = new Relevance(1, posting.getValue());
                scores.merge(posting.getKey(), termScore, Relevance::plus);
            }
        }
        return scores;
    }

    private static List<String> termsOf(String text) {
        return WHITESPACE
                .splitAsStream(text)
                .map(token -> NON_WORD.matcher(token).replaceAll("").toLowerCase(Locale.ROOT))
                .filter(term -> !term.isEmpty())
                .toList();
    }

    private record IndexedDocument(String id, int position) {}

    private record Relevance(int matchedTerms, int occurrences) {
        private Relevance plus(Relevance other) {
            return new Relevance(
                    matchedTerms + other.matchedTerms, occurrences + other.occurrences);
        }
    }
}
