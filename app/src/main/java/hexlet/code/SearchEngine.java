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

        var scores = relevanceByDocument(buildIndex(documents), queryTerms, documents.size());
        var matches = new ArrayList<>(scores.keySet());
        Comparator<IndexedDocument> byRelevance = Comparator.comparingDouble(scores::get);
        matches.sort(byRelevance.reversed().thenComparingInt(IndexedDocument::position));
        return matches.stream().map(IndexedDocument::id).collect(Collectors.toList());
    }

    private static Map<String, Map<IndexedDocument, Integer>> buildIndex(
            List<Map<String, String>> documents) {
        var index = new HashMap<String, Map<IndexedDocument, Integer>>();
        var position = 0;
        for (var document : documents) {
            var terms = termsOf(document.get("text"));
            var indexedDocument = new IndexedDocument(document.get("id"), position++, terms.size());
            for (var term : terms) {
                var postings = index.computeIfAbsent(term, key -> new HashMap<>());
                postings.merge(indexedDocument, 1, Integer::sum);
            }
        }
        return index;
    }

    private static Map<IndexedDocument, Double> relevanceByDocument(
            Map<String, Map<IndexedDocument, Integer>> index,
            Set<String> queryTerms,
            int documentCount) {
        var scores = new HashMap<IndexedDocument, Double>();
        for (var term : queryTerms) {
            var postings = index.getOrDefault(term, Map.of());
            if (postings.isEmpty()) {
                continue;
            }

            var idf = inverseDocumentFrequency(documentCount, postings.size());
            for (var posting : postings.entrySet()) {
                var document = posting.getKey();
                var tf = (double) posting.getValue() / document.termCount();
                scores.merge(document, tf * idf, Double::sum);
            }
        }
        return scores;
    }

    private static double inverseDocumentFrequency(int documentCount, int documentFrequency) {
        var ratio = (documentCount - documentFrequency + 1) / (documentFrequency + 0.5);
        return Math.log(1 + ratio) / Math.log(2);
    }

    private static List<String> termsOf(String text) {
        return WHITESPACE
                .splitAsStream(text)
                .map(token -> NON_WORD.matcher(token).replaceAll("").toLowerCase(Locale.ROOT))
                .filter(term -> !term.isEmpty())
                .toList();
    }

    private record IndexedDocument(String id, int position, int termCount) {}
}
