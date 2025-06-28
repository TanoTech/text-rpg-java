package com.rpg.service.impl;

import com.rpg.character.Character;
import com.rpg.character.CharacterType;
import com.rpg.exceptions.GameException;
import com.rpg.repository.CharacterRepository;
import com.rpg.service.CharacterService;

import java.util.List;

public class CharacterServiceImpl implements CharacterService {
    private final CharacterRepository repository;

    public CharacterServiceImpl(CharacterRepository repository) {
        this.repository = repository;
    }

    @Override
    public Character createCharacter(String type, String name) throws GameException {
        if (repository.existsByName(name)) {
            throw new GameException("Character '" + name + "' already exists");
        }
        return CharacterType.fromString(type).create(name);
    }

    @Override
    public void saveCharacter(Character character) throws GameException {
        if (character == null) throw new GameException("Cannot save null character");
        repository.save(character);
    }

    @Override
    public Character loadCharacter(String name) throws GameException {
        if (name == null || name.trim().isEmpty()) {
            throw new GameException("Character name cannot be empty");
        }
        return repository.findByName(name)
            .orElseThrow(() -> new GameException("Character not found: " + name));
    }

    @Override
    public List<String> getAllSaves() throws GameException {
        return repository.findAllCharacterNames();
    }

    @Override
    public boolean deleteSave(String name) throws GameException {
        if (name == null || name.trim().isEmpty()) {
            throw new GameException("Character name cannot be empty");
        }
        return repository.deleteByName(name);
    }

    @Override
    public boolean characterExists(String name) throws GameException {
        return name != null && !name.trim().isEmpty() && repository.existsByName(name);
    }
}