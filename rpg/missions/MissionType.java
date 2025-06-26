package rpg.missions;

public enum MissionType {
    COMBAT("Combat"),
    EXPLORATION("Exploration"),
    DELIVERY("Delivery"),
    RESCUE("Rescue");

    private final String displayName;

    MissionType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}