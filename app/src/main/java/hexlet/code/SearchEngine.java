package hexlet.code;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class SearchEngine {
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern NON_WORD = Pattern.compile("\\W+");

    public static List<String> search(List<Map<String, String>> documents, String query) {
        var queryTerms = termsOf(query);
        if (queryTerms.size() != 1) {
            return List.of();
        }

        var term = queryTerms.getFirst();
        var matches = new ArrayList<String>();

        for (var document : documents) {
            if (termsOf(document.get("text")).contains(term)) {
                matches.add(document.get("id"));
            }
        }

        return matches;
    }

    private static List<String> termsOf(String text) {
        return WHITESPACE
                .splitAsStream(text)
                .map(token -> NON_WORD.matcher(token).replaceAll("").toLowerCase(Locale.ROOT))
                .filter(term -> !term.isEmpty())
                .toList();
    }
}
