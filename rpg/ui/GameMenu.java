package rpg.ui;

import java.util.LinkedHashMap;
import java.util.Map;

public class GameMenu {
    private String title;
    private Map<String, String> options;

    public GameMenu(String title) {
        this.title = title;
        this.options = new LinkedHashMap<>();
    }

    public void addOption(String key, String description) {
        options.put(key, description);
    }

    public void display() {
        System.out.println("\n=== " + title + " ===");
        for (Map.Entry<String, String> entry : options.entrySet()) {
            System.out.println(entry.getKey() + ". " + entry.getValue());
        }
    }

    public boolean hasOption(String key) {
        return options.containsKey(key);
    }

    public void clearOptions() {
        options.clear();
    }
}
