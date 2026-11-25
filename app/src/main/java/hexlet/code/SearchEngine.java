package hexlet.code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchEngine {
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern NON_WORD = Pattern.compile("\\W+");

    public static List<String> search(List<Map<String, String>> documents, String query) {
        var queryTerms = termsOf(query);
        if (queryTerms.size() != 1) {
            return List.of();
        }

        var term = queryTerms.getFirst();
        var matches = new ArrayList<ScoredDocument>();

        for (var document : documents) {
            var score = relevance(termsOf(document.get("text")), term);
            if (score > 0) {
                matches.add(new ScoredDocument(document.get("id"), score));
            }
        }

        matches.sort(Comparator.comparingInt(ScoredDocument::score).reversed());
        return matches.stream().map(ScoredDocument::id).collect(Collectors.toList());
    }

    private static int relevance(List<String> documentTerms, String queryTerm) {
        return Collections.frequency(documentTerms, queryTerm);
    }

    private static List<String> termsOf(String text) {
        return WHITESPACE
                .splitAsStream(text)
                .map(token -> NON_WORD.matcher(token).replaceAll("").toLowerCase(Locale.ROOT))
                .filter(term -> !term.isEmpty())
                .toList();
    }

    private record ScoredDocument(String id, int score) {}
}
