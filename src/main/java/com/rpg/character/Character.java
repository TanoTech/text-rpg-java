package com.rpg.character;

import com.rpg.equipment.Equipment;
import com.rpg.equipment.EquipmentSlot;
import java.util.EnumMap;
import java.util.Map;

public class Character {
    private final String name;
    private final String characterClass;
    private int level = 1;
    private int health;
    private final int baseHealth;
    private final int baseAttack;
    private int gold = 100;
    private int experience = 0;
    private final Map<EquipmentSlot, Equipment> equipment = new EnumMap<>(EquipmentSlot.class);

    public Character(String name, String characterClass, int health, int attack) {
        this.name = name;
        this.characterClass = characterClass;
        this.health = this.baseHealth = health;
        this.baseAttack = attack;
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
        return baseHealth + (level - 1) * 10;
    }

    public int getAttack() {
        return baseAttack + (level - 1) * 2;
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
        this.health = Math.max(0, Math.min(health, getMaxHealth()));
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
        while (experience >= level * 100) {
            levelUp();
        }
    }

    private void levelUp() {
        experience -= level * 100;
        level++;
        health = getMaxHealth();
        System.out.println("Level up! You are now level " + level);
    }

    public void equipItem(Equipment item) {
        equipment.put(item.getSlot(), item);
    }

    public Equipment unequipItem(EquipmentSlot slot) {
        return equipment.remove(slot);
    }

    public int getTotalAttack() {
        return getAttack() + equipment.values().stream()
                .mapToInt(Equipment::getAttackBonus).sum();
    }

    public int getTotalDefense() {
        return equipment.values().stream()
                .mapToInt(Equipment::getDefenseBonus).sum();
    }

    public String getDetailedInfo() {
        StringBuilder info = new StringBuilder();
        info.append("=== Character Info ===\n");
        info.append("Name: ").append(name).append("\n");
        info.append("Class: ").append(characterClass).append("\n");
        info.append("Level: ").append(level).append("\n");
        info.append("Health: ").append(health).append("/").append(getMaxHealth()).append("\n");
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(name).append(" (").append(characterClass).append(") ===\n")
                .append("Level: ").append(level).append(" | Health: ").append(health).append("/").append(getMaxHealth())
                .append("\n")
                .append("Attack: ").append(getTotalAttack()).append(" | Defense: ").append(getTotalDefense())
                .append("\n")
                .append("Gold: ").append(gold).append(" | Experience: ").append(experience).append("/")
                .append(level * 100).append("\n");

        if (!equipment.isEmpty()) {
            sb.append("Equipment: ");
            equipment.forEach((slot, item) -> sb.append(slot).append(":").append(item.getName()).append(" "));
        }
        return sb.toString();
    }
}