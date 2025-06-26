package rpg.missions;

public class Mission {
    private String title;
    private String description;
    private int difficulty;
    private int reward;
    private MissionType type;

    public Mission(String title, String description, int difficulty, int reward, MissionType type) {
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.reward = reward;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getReward() {
        return reward;
    }

    public MissionType getType() {
        return type;
    }
}