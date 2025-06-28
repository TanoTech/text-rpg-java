package com.rpg.repository;

import com.rpg.character.Character;
import com.rpg.exceptions.DatabaseException;
import java.util.List;
import java.util.Optional;

public interface CharacterRepository {
    void save(Character character) throws DatabaseException;

    Optional<Character> findByName(String name) throws DatabaseException;

    List<String> findAllCharacterNames() throws DatabaseException;

    boolean deleteByName(String name) throws DatabaseException;

    boolean existsByName(String name) throws DatabaseException;
}