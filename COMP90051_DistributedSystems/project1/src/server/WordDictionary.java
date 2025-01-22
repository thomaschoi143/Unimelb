/* WordDictionary.java
   Author: Thomas Choi 1202247 */

package server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class WordDictionary {
    private HashMap<String, LinkedHashSet<String>> dict;
    private DictionaryServer server;

    public WordDictionary(String filepath, DictionaryServer server) throws IOException {
        this.server = server;
        this.dict = new ObjectMapper().readValue(new File(filepath), new TypeReference<HashMap<String, LinkedHashSet<String>>>() {});
    }

    public String search(String word) {
        if (!this.dict.containsKey(word)) {
            return String.format("Error: Could not find word %s", word);
        }
        return String.format("Meanings of %s:\n%s", word, getMeaningsString(word));
    }

    public String addWord(String word, LinkedHashSet<String> meanings) {
        if (this.dict.containsKey(word)) {
            return "Error: Duplicate word";
        }
        if (meanings == null || meanings.size() == 0 || meanings.toString().equals("[]")) {
            return "Error: No meanings";
        }
        dict.put(word, meanings);
        return String.format("Success: Added word %s, with meanings:\n%s", word, getMeaningsString(word));
    }

    public String removeWord(String word) {
        if (!this.dict.containsKey(word)) {
            return String.format("Error: Could not find word %s", word);
        }

        dict.remove(word);
        return String.format("Success: Removed word %s", word);
    }

    public String updateMeaning(String word, LinkedHashSet<String> newMeanings) {
        if (!this.dict.containsKey(word)) {
            return String.format("Error: Could not find word %s", word);
        }
        if (newMeanings == null || newMeanings.size() == 0 || newMeanings.toString().equals("[]")) {
            return "Error: No new meanings";
        }

        LinkedHashSet<String> meanings = dict.get(word);
        meanings.addAll(newMeanings);
        return String.format("Success: Updated word %s, with meanings:\n%s", word, getMeaningsString(word));
    }

    public StatusCode saveDictionary(String filepath) {
        try {
            new ObjectMapper().writeValue(new File(filepath), dict);
            server.getLogger().addMessage("Saved dictionary to " + filepath);
            return StatusCode.HAVE_DICT;
        } catch (IOException e) {
            server.getLogger().addMessage("Failed to save dictionary to " + filepath);
            return StatusCode.NO_DICT;
        }
    }

    private String getMeaningsString(String word) {
        StringBuilder message = new StringBuilder();
        int i = 1;
        for (String m : dict.get(word)) {
            message.append(String.format("%d. %s\n", i++, m));
        }
        return message.toString();
    }
}
