package com.rpg.character;

import com.rpg.exceptions.GameException;
import com.rpg.logging.GameLogger;

public class CharacterFactory {
    private static final GameLogger logger = GameLogger.getInstance();

    public Character createCharacter(String type, String name) throws GameException {
        if (name == null || name.trim().isEmpty()) {
            throw new GameException("Character name cannot be empty");
        }

        Character character = switch (type.toLowerCase()) {
            case "warrior" -> createWarrior(name);
            case "mage" -> createMage(name);
            case "rogue" -> createRogue(name);
            default -> throw new GameException("Unknown character type: " + type);
        };

        logger.info("Created character: " + name + " (" + type + ")");
        return character;
    }

    private Character createWarrior(String name) {
        return new Character(name, "Warrior", 120, 15);
    }

    private Character createMage(String name) {
        return new Character(name, "Mage", 80, 20);
    }

    private Character createRogue(String name) {
        return new Character(name, "Rogue", 60, 25);
    }
}