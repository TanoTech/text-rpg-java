package com.rpg.service;

import com.rpg.character.Character;
import com.rpg.exceptions.GameException;
import java.util.List;

public interface CharacterService {
    Character createCharacter(String type, String name) throws GameException;

    void saveCharacter(Character character) throws GameException;

    Character loadCharacter(String name) throws GameException;

    List<String> getAllSaves() throws GameException;

    boolean deleteSave(String name) throws GameException;

    boolean characterExists(String name) throws GameException;
}
