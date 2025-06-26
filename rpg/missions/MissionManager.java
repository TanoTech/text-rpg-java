package rpg.missions;

import rpg.character.Character;
import rpg.logging.GameLogger;

import java.util.Random;

public class MissionManager {
    private static final GameLogger logger = GameLogger.getInstance();
    private final MissionCollection missionCollection;
    private final Random random;

    public MissionManager() {
        this.missionCollection = new MissionCollection();
        this.random = new Random();
    }

    public Mission generateRandomMission() {
        int index = random.nextInt(missionCollection.size());
        Mission mission = missionCollection.getMission(index);
        logger.info("Generated random mission: " + mission.getTitle());
        return mission;
    }

    public Mission generateMissionByType(MissionType type) {
        var missions = missionCollection.getMissionsByType(type);
        if (missions.isEmpty()) {
            return generateRandomMission();
        }
        Mission mission = missions.get(random.nextInt(missions.size()));
        logger.info("Generated " + type + " mission: " + mission.getTitle());
        return mission;
    }

    public Mission generateMissionByDifficulty(int characterLevel) {
        int minDifficulty = Math.max(1, characterLevel - 2);
        int maxDifficulty = characterLevel + 3;

        var missions = missionCollection.getMissionsByDifficulty(minDifficulty, maxDifficulty);
        if (missions.isEmpty()) {
            return generateRandomMission();
        }
        Mission mission = missions.get(random.nextInt(missions.size()));
        logger.info("Generated level-appropriate mission: " + mission.getTitle());
        return mission;
    }

    public boolean attemptMission(Character character, Mission mission) {
        int characterPower = character.getTotalAttack() + character.getLevel() * 5;
        int missionRequirement = mission.getDifficulty() * 10;

        int roll = random.nextInt(20) + 1;
        int totalPower = characterPower + roll;

        boolean success = totalPower >= missionRequirement;

        logger.info("Mission attempt - Character: " + character.getName() +
                ", Mission: " + mission.getTitle() +
                ", Success: " + success +
                " (Power: " + totalPower + " vs Requirement: " + missionRequirement + ")");

        return success;
    }

    public void displayAllMissions() {
        System.out.println("=== All Available Missions ===");
        int counter = 1;
        for (Mission mission : missionCollection) {
            System.out.println(counter + ". " + mission.getTitle() +
                    " (Difficulty: " + mission.getDifficulty() +
                    ", Reward: " + mission.getReward() + "g, Type: " + mission.getType() + ")");
            counter++;
        }
    }

    public void displayMissionsByType(MissionType type) {
        System.out.println("=== " + type + " Missions ===");
        var iterator = missionCollection.typeIterator(type);
        int counter = 1;
        while (iterator.hasNext()) {
            Mission mission = iterator.next();
            System.out.println(counter + ". " + mission.getTitle() +
                    " (Difficulty: " + mission.getDifficulty() +
                    ", Reward: " + mission.getReward() + "g)");
            counter++;
        }
    }
}