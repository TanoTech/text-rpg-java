package com.rpg.character;

import com.rpg.exceptions.GameException;

public enum CharacterType {
    WARRIOR("Warrior", 120, 15),
    MAGE("Mage", 80, 20),
    ROGUE("Rogue", 60, 25);

    private final String className;
    private final int health;
    private final int attack;

    CharacterType(String className, int health, int attack) {
        this.className = className;
        this.health = health;
        this.attack = attack;
    }

    public Character create(String name) throws GameException {
        if (name == null || name.trim().isEmpty()) {
            throw new GameException("Character name cannot be empty");
        }
        return new Character(name, className, health, attack);
    }

    public static CharacterType fromString(String type) throws GameException {
        try {
            return valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new GameException("Unknown character type: " + type);
        }
    }
}