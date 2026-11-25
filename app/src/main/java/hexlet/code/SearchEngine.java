package hexlet.code;

import java.util.ArrayList;
import java.util.Comparator;
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

        var matches = new ArrayList<ScoredDocument>();
        for (var document : documents) {
            var score = relevance(termsOf(document.get("text")), queryTerms);
            if (score.matchedTerms() > 0) {
                matches.add(new ScoredDocument(document.get("id"), score));
            }
        }

        var relevanceOrder =
                Comparator.comparingInt(Relevance::matchedTerms)
                        .thenComparingInt(Relevance::occurrences);
        matches.sort(Comparator.comparing(ScoredDocument::relevance, relevanceOrder).reversed());
        return matches.stream().map(ScoredDocument::id).collect(Collectors.toList());
    }

    private static Relevance relevance(List<String> documentTerms, Set<String> queryTerms) {
        var matchedTerms = new HashSet<String>();
        var occurrences = 0;
        for (var term : documentTerms) {
            if (queryTerms.contains(term)) {
                matchedTerms.add(term);
                occurrences++;
            }
        }
        return new Relevance(matchedTerms.size(), occurrences);
    }

    private static List<String> termsOf(String text) {
        return WHITESPACE
                .splitAsStream(text)
                .map(token -> NON_WORD.matcher(token).replaceAll("").toLowerCase(Locale.ROOT))
                .filter(term -> !term.isEmpty())
                .toList();
    }

    private record Relevance(int matchedTerms, int occurrences) {}

    private record ScoredDocument(String id, Relevance relevance) {}
}
