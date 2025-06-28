package com.rpg.service;

import com.rpg.character.Character;
import com.rpg.exceptions.GameException;
import com.rpg.missions.Mission;

public interface GameService {
    Mission generateRandomMission() throws GameException;

    boolean attemptMission(Character character, Mission mission) throws GameException;
}