package rpg.character;

import rpg.equipment.Equipment;
import rpg.equipment.EquipmentSlot;
import java.util.HashMap;
import java.util.Map;

public class Character {
    private String name;
    private String characterClass;
    private int level;
    private int health;
    private int maxHealth;
    private int attack;
    private int gold;
    private int experience;
    private Map<EquipmentSlot, Equipment> equipment;

    public Character(String name, String characterClass, int health, int attack) {
        this.name = name;
        this.characterClass = characterClass;
        this.level = 1;
        this.health = health;
        this.maxHealth = health;
        this.attack = attack;
        this.gold = 100;
        this.experience = 0;
        this.equipment = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getCharacterClass() {
        return characterClass;
    }

    public int getLevel() {
        return level;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getAttack() {
        return attack;
    }

    public int getGold() {
        return gold;
    }

    public int getExperience() {
        return experience;
    }

    public Map<EquipmentSlot, Equipment> getEquipment() {
        return equipment;
    }

    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(health, maxHealth));
    }

    public void addGold(int amount) {
        this.gold += amount;
    }

    public boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }

    public void addExperience(int exp) {
        experience += exp;
        checkLevelUp();
    }

    private void checkLevelUp() {
        int expRequired = level * 100;
        if (experience >= expRequired) {
            level++;
            experience -= expRequired;
            maxHealth += 10;
            health = maxHealth;
            attack += 2;
            System.out.println("Level up! You are now level " + level);
        }
    }

    public void equipItem(Equipment item) {
        equipment.put(item.getSlot(), item);
    }

    public Equipment unequipItem(EquipmentSlot slot) {
        return equipment.remove(slot);
    }

    public int getTotalAttack() {
        int total = attack;
        for (Equipment item : equipment.values()) {
            total += item.getAttackBonus();
        }
        return total;
    }

    public int getTotalDefense() {
        int total = 0;
        for (Equipment item : equipment.values()) {
            total += item.getDefenseBonus();
        }
        return total;
    }

    public String getDetailedInfo() {
        StringBuilder info = new StringBuilder();
        info.append("=== Character Info ===\n");
        info.append("Name: ").append(name).append("\n");
        info.append("Class: ").append(characterClass).append("\n");
        info.append("Level: ").append(level).append("\n");
        info.append("Health: ").append(health).append("/").append(maxHealth).append("\n");
        info.append("Attack: ").append(getTotalAttack()).append("\n");
        info.append("Defense: ").append(getTotalDefense()).append("\n");
        info.append("Gold: ").append(gold).append("\n");
        info.append("Experience: ").append(experience).append("\n");

        info.append("\n=== Equipment ===\n");
        if (equipment.isEmpty()) {
            info.append("No equipment equipped.\n");
        } else {
            for (Map.Entry<EquipmentSlot, Equipment> entry : equipment.entrySet()) {
                info.append(entry.getKey()).append(": ").append(entry.getValue().getName()).append("\n");
            }
        }

        return info.toString();
    }
}
