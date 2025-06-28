package com.rpg.service.impl;

import com.rpg.character.Character;
import com.rpg.character.CharacterFactory;
import com.rpg.exceptions.GameException;
import com.rpg.logging.GameLogger;
import com.rpg.repository.CharacterRepository;
import com.rpg.service.CharacterService;

import java.util.List;
import java.util.Optional;

public class CharacterServiceImpl implements CharacterService {
    private static final GameLogger logger = GameLogger.getInstance();
    private final CharacterRepository characterRepository;
    private final CharacterFactory characterFactory;

    public CharacterServiceImpl(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
        this.characterFactory = new CharacterFactory();
    }

    @Override
    public Character createCharacter(String type, String name) throws GameException {
        if (characterExists(name)) {
            throw new GameException("Character with name '" + name + "' already exists");
        }
        return characterFactory.createCharacter(type, name);
    }

    @Override
    public void saveCharacter(Character character) throws GameException {
        if (character == null) {
            throw new GameException("Cannot save null character");
        }
        characterRepository.save(character);
        logger.info("Character service saved: " + character.getName());
    }

    @Override
    public Character loadCharacter(String name) throws GameException {
        if (name == null || name.trim().isEmpty()) {
            throw new GameException("Character name cannot be empty");
        }

        Optional<Character> character = characterRepository.findByName(name);
        return character.orElseThrow(() -> new GameException("Character not found: " + name));
    }

    @Override
    public List<String> getAllSaves() throws GameException {
        return characterRepository.findAllCharacterNames();
    }

    @Override
    public boolean deleteSave(String name) throws GameException {
        if (name == null || name.trim().isEmpty()) {
            throw new GameException("Character name cannot be empty");
        }
        return characterRepository.deleteByName(name);
    }

    @Override
    public boolean characterExists(String name) throws GameException {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return characterRepository.existsByName(name);
    }
}
