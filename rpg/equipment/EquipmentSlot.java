package rpg.equipment;

public enum EquipmentSlot {
    WEAPON("Weapon"),
    ARMOR("Armor"),
    SHIELD("Shield"),
    HELMET("Helmet"),
    BOOTS("Boots");

    private final String displayName;

    EquipmentSlot(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static EquipmentSlot fromString(String s) {
        if (s == null) {
            throw new IllegalArgumentException("EquipmentSlot string is null");
        }
        for (EquipmentSlot slot : values()) {
            if (slot.name().equalsIgnoreCase(s)
                    || slot.displayName.equalsIgnoreCase(s)) {
                return slot;
            }
        }
        throw new IllegalArgumentException("Unknown EquipmentSlot: " + s);
    }
}
