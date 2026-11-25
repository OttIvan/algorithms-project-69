package hexlet.code;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchEngine {
    public static List<String> search(List<Map<String, String>> documents, String query) {
        var matches = new ArrayList<String>();

        for (var document : documents) {
            for (var word : document.get("text").split(" +")) {
                if (!word.isEmpty() && word.equals(query)) {
                    matches.add(document.get("id"));
                    break;
                }
            }
        }

        return matches;
    }
}
