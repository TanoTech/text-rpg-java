package com.rpg.service.impl;

import com.rpg.character.Character;
import com.rpg.exceptions.GameException;
import com.rpg.logging.GameLogger;
import com.rpg.missions.Mission;
import com.rpg.missions.MissionManager;
import com.rpg.service.GameService;
import com.rpg.shop.Shop;

import java.util.Scanner;

public class GameServiceImpl implements GameService {
    private static final GameLogger logger = GameLogger.getInstance();
    private final MissionManager missionManager;
    private final Shop shop;

    public GameServiceImpl() {
        this.missionManager = new MissionManager();
        this.shop = new Shop();
    }

    @Override
    public Mission generateRandomMission() throws GameException {
        Mission mission = missionManager.generateRandomMission();
        logger.info("Generated mission: " + mission.getDescription());
        return mission;
    }

    @Override
    public boolean attemptMission(Character character, Mission mission) throws GameException {
        if (character == null) {
            throw new GameException("Character cannot be null");
        }
        if (mission == null) {
            throw new GameException("Mission cannot be null");
        }

        boolean success = missionManager.attemptMission(character, mission);

        if (success) {
            character.addGold(mission.getReward());
            character.addExperience(mission.getReward() / 2);
            logger.info("Mission completed successfully by " + character.getName());
        } else {
            logger.info("Mission failed for " + character.getName());
        }

        return success;
    }

    @Override
    public void visitShop(Character character, Scanner scanner) throws GameException {
        if (character == null) {
            throw new GameException("Character cannot be null");
        }
        shop.interact(character, scanner);
    }
}