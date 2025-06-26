package rpg.missions;

import java.util.*;

public class MissionCollection implements Iterable<Mission> {
    private List<Mission> missions;

    public MissionCollection() {
        this.missions = new ArrayList<>();
        initializeMissions();
    }

    private void initializeMissions() {
        // Combat missions
        missions.add(new Mission("Goblin Hunt", "Eliminate a group of goblins terrorizing the village",
                3, 50, MissionType.COMBAT));
        missions.add(new Mission("Dragon Slayer", "Defeat the ancient dragon in the mountains",
                10, 500, MissionType.COMBAT));
        missions.add(new Mission("Bandit Patrol", "Clear out the bandit camp near the forest",
                5, 100, MissionType.COMBAT));

        // Exploration missions
        missions.add(new Mission("Lost Treasure", "Find the hidden treasure in the ancient ruins",
                4, 75, MissionType.EXPLORATION));
        missions.add(new Mission("Cave Explorer", "Map the mysterious underground caves",
                6, 120, MissionType.EXPLORATION));
        missions.add(new Mission("Artifact Hunt", "Locate the legendary artifact in the desert",
                8, 200, MissionType.EXPLORATION));

        // Delivery missions
        missions.add(new Mission("Urgent Delivery", "Deliver important documents to the next town",
                2, 30, MissionType.DELIVERY));
        missions.add(new Mission("Medicine Run", "Transport healing potions to the plague-stricken village",
                4, 60, MissionType.DELIVERY));
        missions.add(new Mission("Royal Message", "Deliver a secret message to the king",
                7, 150, MissionType.DELIVERY));

        // Rescue missions
        missions.add(new Mission("Missing Child", "Find and rescue the lost child from the woods",
                3, 40, MissionType.RESCUE));
        missions.add(new Mission("Captured Merchant", "Rescue the merchant from the orc stronghold",
                6, 110, MissionType.RESCUE));
        missions.add(new Mission("Princess Rescue", "Save the princess from the evil wizard's tower",
                9, 300, MissionType.RESCUE));
    }

    public void addMission(Mission mission) {
        missions.add(mission);
    }

    public boolean removeMission(Mission mission) {
        return missions.remove(mission);
    }

    public int size() {
        return missions.size();
    }

    public Mission getMission(int index) {
        if (index >= 0 && index < missions.size()) {
            return missions.get(index);
        }
        return null;
    }

    public List<Mission> getMissionsByType(MissionType type) {
        List<Mission> filtered = new ArrayList<>();
        for (Mission mission : missions) {
            if (mission.getType() == type) {
                filtered.add(mission);
            }
        }
        return filtered;
    }

    public List<Mission> getMissionsByDifficulty(int minDifficulty, int maxDifficulty) {
        List<Mission> filtered = new ArrayList<>();
        for (Mission mission : missions) {
            if (mission.getDifficulty() >= minDifficulty && mission.getDifficulty() <= maxDifficulty) {
                filtered.add(mission);
            }
        }
        return filtered;
    }

    @Override
    public Iterator<Mission> iterator() {
        return new MissionIterator();
    }

    private class MissionIterator implements Iterator<Mission> {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < missions.size();
        }

        @Override
        public Mission next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more missions available");
            }
            return missions.get(currentIndex++);
        }

        @Override
        public void remove() {
            if (currentIndex <= 0) {
                throw new IllegalStateException("Cannot remove mission before calling next()");
            }
            missions.remove(--currentIndex);
        }
    }

    public Iterator<Mission> typeIterator(MissionType type) {
        return getMissionsByType(type).iterator();
    }

    public Iterator<Mission> difficultyIterator(int minDifficulty, int maxDifficulty) {
        return getMissionsByDifficulty(minDifficulty, maxDifficulty).iterator();
    }
}
